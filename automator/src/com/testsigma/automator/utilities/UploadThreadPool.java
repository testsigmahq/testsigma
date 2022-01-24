package com.testsigma.automator.utilities;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Log4j2
public class UploadThreadPool {

  static private UploadThreadPool _instance;
  private ExecutorService executor = null;

  private UploadThreadPool() {
  }

  public static UploadThreadPool getInstance() {
    if (_instance == null) {
      _instance = new UploadThreadPool();
      _instance.createPool();
    }
    return _instance;
  }

  public void createPool() {
    executor = Executors.newFixedThreadPool(100);
  }

  public void closePool() {
    try {
      executor.shutdown();
      executor.awaitTermination(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
    } finally {
      executor.shutdownNow();
    }
    log.info("Closed all upload pool threads");
  }

  public void upload(ScreenshotUploadTask task) {
    executor.execute(task);
  }

}
