package com.testsigma.automator.utilities;

import org.apache.commons.lang3.SystemUtils;

public class OsUtil {

  public String getOsType() {
    String osType = null;

    if (SystemUtils.IS_OS_WINDOWS) {
      osType = "windows";
    } else if (SystemUtils.IS_OS_MAC_OSX) {
      osType = "mac";
    } else if (SystemUtils.IS_OS_LINUX) {
      osType = "linux";
    } else {
      osType = "other";
    }

    return osType;
  }

}
