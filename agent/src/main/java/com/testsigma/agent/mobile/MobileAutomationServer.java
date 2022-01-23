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

@Log4j2
@Component
public class MobileAutomationServer {
  private Process mobileAutomationServerProcess;
  private File androidHome;
  private File jreHome;
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
      this.mobileAutomationServerExecutablePath = new File(PathUtil.getInstance().getMobileAutomationServerPath(), "appium");
      if (SystemUtils.IS_OS_WINDOWS) {
        this.mobileAutomationServerExecutablePath = new File(PathUtil.getInstance().getMobileAutomationServerPath(), "appium.exe");
      }
      this.serverIpAddress = serverIP();
      this.logFilePath = new File(PathUtil.getInstance().getLogsPath() + File.separator + "appium.log");
      Integer serverPort = NetworkUtil.getFreePort();
      this.serverURL = String.format("http://%s:%d/wd/hub", serverIpAddress, serverPort);

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
