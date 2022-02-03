package com.testsigma.agent.launcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Objects;

public class  Application {
  private static final String STOP_COMMAND = "stop";
  private static final int GRACEFUL_SHUTDOWN_THRESH_HOLD = 60;
  private static final Logger log = LogManager.getLogger(Application.class);

  public static void main(String[] paramArrayOfString) {
    if (paramArrayOfString.length >= 1 && STOP_COMMAND.equalsIgnoreCase(paramArrayOfString[0])) {
      stop();
    } else {
      start();
    }
    Runtime.getRuntime().halt(0);
  }

  private static void start() {
    log.info("-------------------- Testsigma Agent - START -------------------");
    try {
      File lockFile = new File(Objects.requireNonNull(Config.getDataDir()) + File.separator + "lock");
      File pidFile = new File(Objects.requireNonNull(Config.getDataDir()) + File.separator + "process.pid");
      log.info("Lock File Location: " + lockFile.getAbsolutePath());
      log.info("PID File Location: " + pidFile.getAbsolutePath());
      RandomAccessFile randomAccessFile = new RandomAccessFile(lockFile, "rw");
      FileChannel fileChannel = randomAccessFile.getChannel();
      FileLock fileLock = fileChannel.tryLock();
      if (fileLock != null) {
        try {
          Thread.currentThread().setName("TestsigmaAgentWrapper");
          createPidFile(pidFile);
          Launcher.getInstance().launch().join();
          removePidFile(pidFile);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
        log.info("Releasing Lock On Testsigma Agent Lock File...");
        fileLock.release();
        fileChannel.close();
        randomAccessFile.close();
        boolean lockDeleted = lockFile.delete();
        log.info("Testsigma Agent Lock File " + lockFile.getAbsolutePath() + " Deleted " + lockDeleted);
      } else {
        log.info("Failed To Launch Testsigma Agent - Another Instance Of Testsigma Agent Is Already Running!");
        fileChannel.close();
        randomAccessFile.close();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    log.info("-------------------- Testsigma Agent - STOPPED -------------------");
  }

  private static void createPidFile(File pidFile) throws IOException {
    removePidFile(pidFile);
    RandomAccessFile pidFileWriter = new RandomAccessFile(pidFile, "rw");
    long processPid = ProcessHandle.current().pid();
    log.info("Testsigma Agent Main PID - " + processPid);
    pidFileWriter.writeBytes(processPid + "");
    pidFileWriter.close();
  }

  private static void removePidFile(File pidFile) {
    if (pidFile.exists()) {
      log.debug("Testsigma Agent Main PID File Exists - " + pidFile.getAbsolutePath());
      boolean pidFileDeleted = pidFile.delete();
      log.debug("Testsigma Agent Main PID File " + pidFile.getAbsolutePath() + " Deleted - " + pidFileDeleted);
    } else {
      log.debug("Testsigma Agent Main PID File Doesn't Exists - " + pidFile.getAbsolutePath());
    }
  }

  private static void stop() {
    try {
      log.info("Stopping Testsigma Agent Using Stop Command");
      File pidFile = new File(Objects.requireNonNull(Config.getDataDir()) + File.separator + "process.pid");
      log.info("PID File Location: " + pidFile.getAbsolutePath());
      RandomAccessFile pidFileReader = new RandomAccessFile(pidFile, "r");
      String pid = pidFileReader.readLine();

      pidFileReader.close();
      if (StringUtils.isNoneBlank(pid)) {
        ProcessHandle processHandle = ProcessHandle.of(Long.parseLong(pid)).get();
        if (processHandle.supportsNormalTermination()) {
          log.debug("Process Implementation Supports Normal Termination. Terminating the Process Normally");
          processHandle.destroy();
        } else {
          log.debug("Process Implementation Doesn't Support Normal Termination(Probably Windows / VM). Destroying Parent and Child Processes Forcefully");
          stopProcessForcefully(processHandle);
        }
        for (int i = 0; i < GRACEFUL_SHUTDOWN_THRESH_HOLD; i += 5) {
          Thread.sleep(5000);
          if (!processHandle.isAlive()) {
            break;
          } else {
            log.info("Waiting For Testsigma Agent To Stop");
          }
        }

        if (processHandle.isAlive()) {
          log.info("Testsigma Agent Was Not Stopped Gracefully. Shutting It Down Forcefully");
          stopProcessForcefully(processHandle);
        } else {
          log.info("Stopped Testsigma Agent Gracefully");
        }
        if (pidFile.exists()) {
          removePidFile(pidFile);
        }
      } else {
        log.error("Process PID not found in PID file - " + pidFile.getAbsolutePath());
      }
    } catch (IOException | InterruptedException e) {
      log.error(e.getMessage(), e);
    }
  }

  private static void stopProcessForcefully(ProcessHandle processHandle) {
    try {
      Thread.sleep(5000);
      processHandle.children().forEach(Application::stopProcessForcefully);
      processHandle.destroyForcibly();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
