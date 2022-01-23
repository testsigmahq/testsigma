/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.utils;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;

@Log4j2
public class PathUtil {

  private static PathUtil _instance = null;

  private String classPathSeparator = null;

  @Getter
  private String testsigmaDataPath = null;
  @Getter
  private String logsPath = null;
  @Getter
  private String tempPath = null;
  @Getter
  private String driversPath = null;
  @Getter
  private String jrePath = null;
  @Getter
  private String androidPath = null;
  @Getter
  private String iosPath = null;
  @Getter
  private String mobileAutomationServerPath = null;
  @Getter
  private String upgradePath = null;
  @Getter
  private String rootPath = null;
  @Getter
  private String configPath = null;
  @Getter
  private String pendingExecutionPath = null;
  @Getter
  private String classPath = null;
  @Getter
  private String additionalLibPath = null;

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
    classPathSeparator = (SystemUtils.IS_OS_WINDOWS ? ";" : ":");
    rootPath = System.getProperty("TS_ROOT_DIR");
    testsigmaDataPath = System.getProperty("TS_DATA_DIR");

    driversPath = rootPath + File.separator + "drivers";
    jrePath = rootPath + File.separator + "jre";
    mobileAutomationServerPath = rootPath + File.separator + "appium";
    androidPath = rootPath + File.separator + "android";
    iosPath = rootPath + File.separator + "ios";

    logsPath = testsigmaDataPath + File.separator + "logs";
    tempPath = testsigmaDataPath + File.separator + "temp";
    upgradePath = testsigmaDataPath + File.separator + "upgrade";
    configPath = testsigmaDataPath + File.separator + "config";
    pendingExecutionPath = testsigmaDataPath + File.separator + "pending_executions";
    additionalLibPath = testsigmaDataPath + File.separator + "additional_libs";

    classPath = ClassPathUtil.getClassPath() + this.classPathSeparator
      + ClassPathUtil.setClasspathFromDirectory(this.additionalLibPath);

    initialized = true;
    log.info("class path - " + classPath);
  }
}
