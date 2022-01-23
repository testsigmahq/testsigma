package com.testsigma.agent.launcher;

import org.apache.commons.lang3.SystemUtils;

import java.nio.file.Paths;

public class Config {

  public static String getDataDir() {
    if (SystemUtils.IS_OS_MAC)
      return System.getProperty("TS_DATA_DIR", Paths.get(System.getProperty("user.home"), "Library",
        "Application Support", "Testsigma", "Agent").toString());

    if (SystemUtils.IS_OS_LINUX)
      return System.getProperty("TS_DATA_DIR",
        Paths.get(System.getProperty("user.home"), ".testsigma", "agent").toString());

    if (SystemUtils.IS_OS_WINDOWS)
      return System.getProperty("TS_DATA_DIR",
        Paths.get(System.getenv("AppData"), "Testsigma", "agent").toString());

    return null;
  }
}
