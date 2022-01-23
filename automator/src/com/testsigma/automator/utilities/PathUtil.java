/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.utilities;

import lombok.Getter;

import java.io.File;

public class PathUtil {
  private static PathUtil _instance = null;

  @Getter
  private String driversPath = null;
  @Getter
  private String rootPath = null;
  @Getter
  private String testsigmaDataPath = null;
  @Getter
  private String screenshotsPath = null;
  @Getter
  private String videosPath = null;
  @Getter
  private String logPath = null;
  @Getter
  private String customClassesPath = null;
  @Getter
  private String uploadPath = null;
  @Getter
  private String iosPath = null;

  private boolean initialized = false;

  private PathUtil() {
  }

  public static PathUtil getInstance() {
    if (_instance == null) {
      _instance = new PathUtil();
      _instance.setPathsFromContext();
    }
    return _instance;
  }

  public void setPathsFromContext() {
    setPathsFromContext(false);
  }

  public void setPathsFromContext(boolean reset) {
    if (initialized & !reset) {
      return;
    }
    rootPath = System.getProperty("TS_ROOT_DIR");
    testsigmaDataPath = System.getProperty("TS_DATA_DIR");

    driversPath = rootPath + File.separator + "drivers";
    iosPath = rootPath + File.separator + "ios";

    screenshotsPath = testsigmaDataPath + File.separator + "screenshots";
    videosPath = testsigmaDataPath + File.separator + "videos";
    logPath = testsigmaDataPath + File.separator + "logs";
    customClassesPath = testsigmaDataPath + File.separator + "custom_classes";
    uploadPath = testsigmaDataPath + File.separator + "upload";
    initialized = true;
  }

}
