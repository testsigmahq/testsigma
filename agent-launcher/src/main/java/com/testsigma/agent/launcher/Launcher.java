package com.testsigma.agent.launcher;

import dorkbox.systemTray.Entry;
import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static javax.swing.JOptionPane.showMessageDialog;

public class Launcher {
  private static final Logger log = LoggerFactory.getLogger(Launcher.class);
  private static final String classPathSeparator = (SystemUtils.IS_OS_WINDOWS ? ";" : ":");
  private static Launcher _instance;
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  private boolean background = false;
  private boolean running = true;
  private boolean restart = false;
  private SystemTray systemTray = null;
  private ExecutorService socketExecutorService;
  private CompletableFuture<?> completableFuture;
  private Future<?> future;
  private ServerSocket wrapperServer;
  private Socket ipcSocket;
  private Process agentProcess;
  private AgentStatus agentStatus;
  private Thread shutdownHookThread;
  private CloseableHttpClient client;

  public static Launcher getInstance() {
    if (_instance == null) {
      _instance = new Launcher();
    }
    return _instance;
  }

  public final CompletableFuture<?> launch() {
    log.info("Launching Agent...");
    this.shutdownHookThread = new Thread(this::shutdownLauncher);
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);
    configureSystemTrayIcon();
    start();
    return (this.completableFuture = new CompletableFuture());
  }

  private void start() {
    waitForAgentToStop();
    Launcher launcher = this;
    this.executorService.submit(() -> {
      List<String> command;
      launcher.agentProcess = null;
      log.info("Starting Agent...");
      setStatus(AgentStatus.STARTING);
      try {
        launcher.startWrapperServer();
        this.future = launcher.socketExecutorService.submit(() -> {
          Thread.currentThread().setName("agent-launcher-server");
          this.startIpcSocket();
          this.cleanupAgentProcess();
          shutdownLauncher();
          if (!this.background) {
            this.systemTray.shutdown();
          }
          //Commenting out restart option dues continuous restart when web server config fetch fails
          //this.restart();
        });

        command = launcher.agentStartCommand(launcher.wrapperServer.getLocalPort());
        launcher.agentProcess = launcher.startAgentProcess(command);
        log.debug("Waiting for Agent to start...");
        int agentStartupChecks = 60;

        while (agentStartupChecks > 0) {
          try {
            log.debug("Waiting for Agent to start");
            TimeUnit.SECONDS.sleep(1L);
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
          if (!launcher.agentProcess.isAlive()) {
            log.error("Agent exited unexpectedly with exit code - " + launcher.agentProcess.exitValue());
            break;
          }
          if (launcher.ipcSocket != null) break;
          agentStartupChecks--;
        }
        if (launcher.ipcSocket != null) {
          log.info("Agent started successfully with process - " + agentProcess.pid());
          setStatus(AgentStatus.STARTED);
        } else {
          log.error("Failed to start agent....");
        }
      } catch (Exception e) {
        launcher.completableFuture.completeExceptionally(e);
      } finally {
        handleFailedStart();
      }
    });
  }

  private void restart() {
    if (this.running) {
      if (this.restart) {
        log.info("Agent restart requested");
        this.restart = false;
      }
      if (this.agentProcess != null && this.agentProcess.exitValue() != 0) {
        log.info("Agent exit was not clean. Exit code - " + this.agentProcess.exitValue());
        this.shutdown();
        return;
      }
      log.info("Starting Agent again....");
      this.start();
    }
  }

  private void shutdownLauncher() {
    Thread.currentThread().setName("shutdown-hook");
    this.running = false;
    shutdown();
    waitForAgentToStop();
    this.completableFuture.complete(null);
    log.info("Shutting down Launcher");
  }

  private void shutdown() {
    if (this.agentStatus.equals(AgentStatus.STOPPING) || this.agentStatus.equals(AgentStatus.STOPPED))
      return;
    log.info("Agent shutdown initiated");
    Process process = this.agentProcess;
    setStatus(AgentStatus.STOPPING);
    shutdownAgent();
    setStatus(AgentStatus.STOPPED);
    Executors.newSingleThreadScheduledExecutor().schedule(() -> {
      try {
        if (process.isAlive()) {
          log.info("Stopping agent process forcibly since agent process didn't exit normally");
          process.destroyForcibly();
        }
      } catch (Exception exception) {
        log.error(exception.getMessage(), exception);
      }
    }, 10L, TimeUnit.SECONDS);
  }

  private void shutdownAgent() {
    stopIpcSocket();
    stopWrapperServer();
    stopFuture();
    stopIpcSocketExecutorService();
  }

  private void waitForAgentToStop() {
    while (this.agentProcess != null && this.agentProcess.isAlive()) {
      log.info("Agent is still running, waiting");
      try {
        TimeUnit.SECONDS.sleep(1L);
      } catch (InterruptedException ignored) {
      }
    }
  }

  private void setStatus(AgentStatus agentStatus) {
    this.agentStatus = agentStatus;
    log.info("Changed Agent status to - " + agentStatus);
    if (this.background)
      return;
    this.systemTray.setStatus(String.format("Testsigma Agent - %s", agentStatus));
  }

  private List<String> agentStartCommand(int wrapperPort) throws URISyntaxException {
    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    List<String> list = runtimeMXBean.getInputArguments();
    List<String> commandLineParameters = new ArrayList<>();
    for (String listStr : list) {
      if (!listStr.startsWith("-agentlib")) {
        commandLineParameters.add(listStr);
      }
    }
    List<String> command = new ArrayList<>();
    command.add(getJavaPath());
    command.addAll(commandLineParameters);
    command.add("-cp");
    command.add(getAgentClassPath());
    command.add("-Dagent.wrapper.port=" + wrapperPort);
    command.add("-Dagent.wrapper.background=" + this.background);
    command.add("com.testsigma.agent.TestsigmaAgent");
    return command;
  }

  private void sendFlare() {
    String alertMessage = "Unknown Error";
    try {
      client = HttpClients.createDefault();
      HttpGet getRequest = new HttpGet("http://localhost:8383/agent/api/v1/flare");
      HttpResponse response = client.execute(getRequest);
      if (response.getEntity() != null) {
        alertMessage = EntityUtils.toString(response.getEntity());
      }

      log.info("Response from flare request - " + response.getStatusLine() + " - " + alertMessage);
    } catch (Exception e) {
      alertMessage = e.getMessage();
      log.error(e.getMessage(), e);
    } finally {
      showMessageDialog(null, alertMessage);
      HttpClientUtils.closeQuietly(client);
    }
  }

  private void configureSystemTrayIcon() {
    if (GraphicsEnvironment.isHeadless()) {
      log.info("No Graphics environment available - headless mode.");
      this.background = true;
    }
    if (!this.background) {
      log.info("Loading System Tray icon");
      this.systemTray = SystemTray.get();
      InputStream inputStream = Launcher.class.getClassLoader().getResourceAsStream("icons/tray_icon.png");
      systemTray.setImage(inputStream);
      setStatus(AgentStatus.STOPPED);
      systemTray.getMenu().add((Entry) new dorkbox.systemTray.MenuItem("Send Flare Request", (ActionEvent actionEvent) -> {
        log.info("Agent send flare request menu action triggered");
        MenuItem menuItem = ((MenuItem) actionEvent.getSource());
        menuItem.setEnabled(false);
        sendFlare();
        menuItem.setEnabled(true);
      }));

//      systemTray.getMenu().add((Entry) new dorkbox.systemTray.MenuItem("Restart", (ActionEvent actionEvent) -> {
//        log.info("Agent restart menu action triggered");
//        this.restart = true;
//        shutdown();
//      }));

      systemTray.getMenu().add((Entry) new MenuItem("Quit", (ActionEvent actionEvent) -> {
        log.info("Agent quit menu action triggered");
        Runtime.getRuntime().removeShutdownHook(this.shutdownHookThread);
        shutdownLauncher();
        if (!this.background) {
          this.systemTray.shutdown();
        }
      }));
      this.setupFrame();
    }
  }

  public void handleFailedStart() {
    if (this.ipcSocket == null) {
      if ((this.agentProcess != null) && agentProcess.isAlive()) {
        this.agentProcess.destroyForcibly();
      }
      this.shutdownAgent();
    }
  }

  private String getJavaPath() {
    String rootDir = System.getProperty("TS_ROOT_DIR");
    if (StringUtils.isNotBlank(rootDir)) {
      System.setProperty("java.home", rootDir + File.separator + "jre");
    }
    return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
  }

  private String getAgentClassPath() throws URISyntaxException {
    String classPath = System.getProperty("TS_AGENT_JAR") + File.separator + "lib" + File.separator + "*";
    String additionalClassPath = Config.getDataDir() + File.separator + "additional_libs" + File.separator + "*";
    String agentJarPath = getAgentJarPath();
    return agentJarPath + classPathSeparator + classPath + classPathSeparator + additionalClassPath;
  }

  private String getAgentJarPath() throws URISyntaxException {
    String agentJarDir = System.getProperty("TS_AGENT_JAR");
    if (StringUtils.isNotBlank(agentJarDir)) {
      return agentJarDir + File.separator + "agent.jar";
    }
    URL uRL = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
    Path path = (new File(uRL.toURI())).toPath().getParent();
    return Paths.get(path.toAbsolutePath().toString(), "agent.jar").toAbsolutePath().toString();
  }

  private void startIpcSocket() {
    log.info("Accepting connection to the launcher socket....");
    try {
      this.ipcSocket = this.wrapperServer.accept();
      log.info("Agent connected to IPC socket...");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    if (this.ipcSocket != null) {
      try {
        int data = this.ipcSocket.getInputStream().read();
        while (data != -1) {
          data = this.ipcSocket.getInputStream().read();
        }
      } catch (Exception e) {
        log.info("Agent disconnected from IPC socket");
      }
    } else {
      log.error("Agent not connected to IPC socket");
    }
  }

  private void stopIpcSocket() {
    if (this.ipcSocket != null) {
      try {
        this.ipcSocket.close();
      } catch (IOException e) {
        log.error(e.getMessage(), e);
      }
      this.ipcSocket = null;
    }
  }

  private void startWrapperServer() throws IOException {
    this.wrapperServer = new ServerSocket(0);
    log.info("Agent wrapper server started on port " + this.wrapperServer.getLocalPort());
    this.socketExecutorService = Executors.newSingleThreadExecutor();
  }

  private void stopWrapperServer() {
    if (this.wrapperServer != null) {
      try {
        this.wrapperServer.close();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
      this.wrapperServer = null;
    }
  }

  private void stopFuture() {
    try {
      if (this.future != null && !this.future.isDone()) {
        this.future.get(10L, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      this.future.cancel(true);
    }
  }

  private void stopIpcSocketExecutorService() {
    if (this.socketExecutorService != null && !this.socketExecutorService.isTerminated()) {
      try {
        this.socketExecutorService.shutdown();
        if (!this.socketExecutorService.awaitTermination(10L, TimeUnit.SECONDS)) {
          log.warn("Failed to stop socketExecutorService in timely manner, force stopping...");
          this.socketExecutorService.shutdownNow();
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        this.socketExecutorService.shutdownNow();
      }
    }
  }

  private void cleanupAgentProcess() {
    if (this.agentProcess != null) {
      try {
        if (this.agentProcess.waitFor(20L, TimeUnit.SECONDS)) {
          log.info("Agent process stopped. Exit code - " + this.agentProcess.exitValue());
        } else {
          log.error("Agent process is taking too long to exit. Forcing agent to shutdown forcefully....");
          this.agentProcess.destroyForcibly();
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        this.agentProcess.destroyForcibly();
      }
    }
  }

  private Process startAgentProcess(List<String> command) throws IOException {
    log.debug("Starting Agent using command: " + String.join(" ", command));
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    processBuilder.environment().put("JAVA_HOME", System.getProperty("java.home"));
    processBuilder.environment().put("PATH", System.getenv("PATH") + File.pathSeparator + System.getProperty("java.home"));
    processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
    processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
    return processBuilder.start();
  }

  private void setupFrame() {
    try {
      final Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
      final URL imageResource = Launcher.class.getClassLoader().getResource("icons/dock_icon.png");
      final Image image = defaultToolkit.getImage(imageResource);
      final Taskbar taskbar = Taskbar.getTaskbar();

      try {
        taskbar.setIconImage(image);
      } catch (final UnsupportedOperationException e) {
        log.info("The os does not support task bar setIconImage ");
      } catch (final SecurityException e) {
        log.error(e.getMessage(), e);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
