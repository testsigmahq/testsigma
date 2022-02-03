/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.model.Browsers;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HybridPlatformService {
  private static final String BROWSER_VERSION_LATEST = "Latest";

  public String getDriverPath(String os, String browserVersion, Browsers browser, String versionFolder) {
    String name = browser.getFileName();
    if (browser.equals(Browsers.MicrosoftEdge)) {
      name = getEdgeDriverName(browserVersion, name);
    }
    String driverName = getDriverFileName(os, name);
    return File.separator + browser.getBrowserFolderName() + File.separator
      + versionFolder + File.separator + driverName;
  }

  public String getEdgeDriverName(String browserVersion, String name) {
    String returnName = name;
    try {
      int versionNumber = (int) Double.parseDouble(browserVersion);
      returnName = (versionNumber >= 80) ? "msedgedriver" :
        "MicrosoftWebDriver";
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return returnName;
  }

  public String getDriverFileName(String os, String name) {
    return os.toLowerCase().contains("windows") ? name + ".exe" : name;
  }

  public void closePlatformSession() {
    return;
  }
}
