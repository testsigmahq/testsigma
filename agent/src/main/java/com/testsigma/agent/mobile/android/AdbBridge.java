/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile.android;

import com.testsigma.agent.utils.PathUtil;
import com.android.ddmlib.AndroidDebugBridge;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.File;

@Component
@Log4j2
public class AdbBridge {
  private static AndroidDebugBridge adbInstance;
  private static boolean bridgeInitialized = false;

  public void createBridge() {
    if (!bridgeInitialized) {
      log.info("Initialising Android Bridge");
      AndroidDebugBridge.initIfNeeded(false);
      adbInstance = AndroidDebugBridge.createBridge(getAdbFile().getAbsolutePath(), true);
      bridgeInitialized = true;
    }
  }

  @PreDestroy
  public void closeBridge() {
    try {
      if (bridgeInitialized) {
        log.info("Closing Android Bridge");
        AndroidDebugBridge.disconnectBridge();
        AndroidDebugBridge.terminate();
        bridgeInitialized = false;
        adbInstance = null;
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public AndroidDebugBridge getADBInstance() {
    return adbInstance;
  }

  public File getAdbFile() {
    String os = System.getProperty("os.name").toUpperCase();
    String adbToolPath = PathUtil.getInstance().getAndroidPath() + File.separator + "platform-tools";

    if (os.startsWith("WIN")) {
      adbToolPath = adbToolPath + File.separator + "adb.exe";
    } else {
      adbToolPath = adbToolPath + File.separator + "adb";
    }
    return new File(adbToolPath);
  }
}
