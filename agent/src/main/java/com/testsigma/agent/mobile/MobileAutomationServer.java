/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile;


import com.testsigma.agent.utils.NetworkUtil;
import com.testsigma.agent.utils.PathUtil;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.stereotype.Component;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Log4j2
@Component
public class MobileAutomationServer {
  private Process mobileAutomationServerProcess;
  private File androidHome;
  private File jreHome;

  private File appiumHome;
  private File mobileAutomationServerExecutablePath;
  private File logFilePath;
  private String serverIpAddress;
  @Getter
  private Boolean running = false;

  @Getter
  private String serverURL;

  private File androidHome() {
    return new File(PathUtil.getInstance().getAndroidPath());
  }

  private File jreHome() {
    return new File(PathUtil.getInstance().getJrePath());
  }

  private File appiumHome(){
    return new File(PathUtil.getInstance().getMobileAutomationDriverPath());
  }

  private String serverIP() {
    String address = "127.0.0.1";
    try {
      address = InetAddress.getByName("localhost").getHostAddress();
    } catch (UnknownHostException unknownHostException) {
      log.info("Ignoring unknownHostException");
    }
    return address;
  }

  public void start() {
    try {
      if (this.running) {
        log.info("Mobile automation server is already running...so not starting it again...");
        return;
      }
      this.androidHome = androidHome();
      this.jreHome = jreHome();
      this.appiumHome = appiumHome();
      this.mobileAutomationServerExecutablePath = new File(PathUtil.getInstance().getMobileAutomationServerPath(), "appium");
      if (SystemUtils.IS_OS_WINDOWS) {
        this.mobileAutomationServerExecutablePath = new File(PathUtil.getInstance().getMobileAutomationServerPath(), "appium.exe");
      }
      this.serverIpAddress = serverIP();
      this.logFilePath = new File(PathUtil.getInstance().getLogsPath() + File.separator + "appium.log");
      Integer serverPort = NetworkUtil.getFreePort();
      this.serverURL = String.format("http://%s:%d", serverIpAddress, serverPort);
      extractMobileAutomationDrivers();
      log.info("Starting Mobile Automation Server at - " + serverURL);

      (new Thread(() -> {
        try {
          ProcessBuilder processBuilder =
            new ProcessBuilder(mobileAutomationServerExecutablePath.getAbsolutePath(),
              "--address", serverIpAddress,
              "--port", serverPort.toString(),
              "--log-level", "debug",
              "--log-no-colors",
              "--session-override",
              "--log-timestamp",
              "--allow-insecure", "chromedriver_autodownload");
          processBuilder.directory(new File(PathUtil.getInstance().getMobileAutomationServerPath()));
          processBuilder.environment().put("APPIUM_HOME", appiumHome.getAbsolutePath());
          processBuilder.environment().put("ANDROID_HOME", androidHome.getAbsolutePath());
          processBuilder.environment().put("JAVA_HOME", jreHome.getAbsolutePath());
          processBuilder.redirectErrorStream(true);
          processBuilder.redirectOutput(logFilePath);
          mobileAutomationServerProcess = processBuilder.start();
          this.running = false;
          log.info("Mobile Automation Server Started...");
        } catch (IOException e) {
          log.error(e.getMessage(), e);
        }
      })).start();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  private void extractMobileAutomationDrivers(){
    log.info("Trying to check if driver folder is present inside Appium directory");
    try{
      if (!isDriverFolderExists()) {
        log.info("Driver folder does not exits.Extracting from the zip files");
        File androidDriverLocalPath = Paths.get(PathUtil.getInstance().getMobileAutomationServerPath(), "appium-uiautomator2-driver.zip").toFile();
        File iOsDriverLocalPath = Paths.get(PathUtil.getInstance().getMobileAutomationServerPath(), "appium-xcuitest-driver.zip").toFile();
        File destinationPath = new File(PathUtil.getInstance().getMobileAutomationDriverDestinationPath());
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<?> unzipAndroidDriver = executor.submit(() -> {
          try {
            unzipDriver(androidDriverLocalPath,destinationPath);
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        });
        Future<?> unzipIosDriver = executor.submit(() -> {
          try {
            unzipDriver(iOsDriverLocalPath,destinationPath);
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          }
        });
        try {
          // Wait for the tasks to complete within the timeout duration
          int timeoutSeconds = 120;
          unzipAndroidDriver.get(timeoutSeconds, TimeUnit.SECONDS);
          unzipIosDriver.get(timeoutSeconds, TimeUnit.SECONDS);
          log.info("Unzip completed successfully");
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        } finally {
          executor.shutdown();
        }
      }
    } catch (Exception e){
      log.error(e.getMessage(), e);
    }
  }

  private boolean isDriverFolderExists() {
    String dirPath = appiumHome.getAbsolutePath();
    log.info("Verifying if driver folder exists: " + dirPath);
    File driverDirectoryFile = new File(dirPath);
    if (driverDirectoryFile.exists()) {
      File driverFile = new File(driverDirectoryFile.getAbsolutePath());
      return driverFile.exists() && driverFile.isDirectory();
    }
    return false;
  }

  private void unzipDriver(File sourcePath,File destinationPath) throws IOException {
    File fileZip = new File(String.valueOf(sourcePath));
    File destDir = new File(String.valueOf(destinationPath));

    try (ZipFile zipFile = new ZipFile(fileZip)) {
      Enumeration<? extends ZipEntry> entries = zipFile.entries();

      while (entries.hasMoreElements()) {
        ZipEntry entry = entries.nextElement();
        if (!entry.isDirectory()) {
          String entryName = entry.getName();
          // to skip creating the "_MACOSX" directory
          if (!entryName.startsWith("__MACOSX")) {
            File entryFile = new File(destDir, entryName);
            Files.createDirectories(entryFile.getParentFile().toPath());
            Files.copy(zipFile.getInputStream(entry), entryFile.toPath());
          }
        }
      }
    } catch (Exception e) {
       log.error(e.getMessage(),e);
    }
  }

  @PreDestroy
  public void stop() {
    log.info("Stopping Mobile Automation Server...");
    try {
      if (this.mobileAutomationServerProcess != null && this.mobileAutomationServerProcess.isAlive()) {
        this.mobileAutomationServerProcess.destroyForcibly();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.info("Mobile Automation Server Stopped...");
  }
}
