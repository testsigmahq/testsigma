package com.testsigma.agent.init;

import lombok.extern.log4j.Log4j2;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Log4j2
public class WrapperConnector {
  private static WrapperConnector _instance = null;
  private ExecutorService executorService;
  private Future<?> future;
  private Socket wrapperSocket;

  public static WrapperConnector getInstance() {
    if (_instance == null) {
      _instance = new WrapperConnector();
    }
    return _instance;
  }

  public void connect() {
    if (executorService != null && !executorService.isTerminated()) {
      try {
        executorService.shutdownNow();
        executorService = null;
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    executorService = Executors.newSingleThreadExecutor();
    this.future = executorService.submit(() -> {
      String wrapperPortStr = System.getProperty("agent.wrapper.port");
      if (wrapperPortStr != null) {
        try {
          int wrapperPort = Integer.parseInt(wrapperPortStr);
          log.info("Connecting To Wrapper Socket Using Port - " + wrapperPortStr);
          wrapperSocket = new Socket("localhost", wrapperPort);
          int data = wrapperSocket.getInputStream().read();
          while (data != -1) {
            data = wrapperSocket.getInputStream().read();
          }
          log.info("Disconnected From Wrapper Socket...");
          wrapperSocket.close();
          wrapperSocket = null;
          Runtime.getRuntime().exit(0);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    });
  }

  public void disconnect() {
    log.info("Wrapper disconnect method triggered by agent process");
    try {
      if (this.future != null && !this.future.isDone()) {
        this.future.cancel(true);
      }

      if (wrapperSocket != null) {
        wrapperSocket.close();
        wrapperSocket = null;
      }

      if (executorService != null && !executorService.isTerminated()) {
        try {
          executorService.shutdown();
          if (!executorService.awaitTermination(5L, TimeUnit.SECONDS)) {
            log.warn("Failed to stop wrapper disconnect executor service in timely manner, force stopping...");
            executorService.shutdownNow();
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          executorService.shutdownNow();
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    Runtime.getRuntime().halt(0);
  }

  public void disconnectHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> WrapperConnector.getInstance().disconnect()));
  }

  public void shutdown() {
    if (this.wrapperSocket == null) {
      Runtime.getRuntime().exit(0);
    } else {
      this.disconnect();
    }
  }
}
