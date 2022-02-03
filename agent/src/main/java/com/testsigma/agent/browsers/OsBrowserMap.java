package com.testsigma.agent.browsers;

import com.testsigma.automator.entity.OsBrowserType;
import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;

import java.util.HashMap;

public class OsBrowserMap {

  private static OsBrowserMap _instance = null;

  @Getter
  private HashMap<OsBrowserType, String> browserMap = null;

  public OsBrowserMap() {
    if (SystemUtils.IS_OS_MAC) {
      this.browserMap = initMacBrowserMap();
    } else if (SystemUtils.IS_OS_WINDOWS) {
      this.browserMap = initWindowsBrowserMap();
    } else if (SystemUtils.IS_OS_LINUX) {
      this.browserMap = initLinuxBrowserMap();
    }
  }

  public static OsBrowserMap getInstance() {
    if (_instance == null)
      _instance = new OsBrowserMap();

    return _instance;
  }

  public HashMap<OsBrowserType, String> initMacBrowserMap() {
    HashMap<OsBrowserType, String> browsers = new HashMap<OsBrowserType, String>();
    browsers.put(OsBrowserType.Chrome, "Google Chrome");
    browsers.put(OsBrowserType.Firefox, "Firefox");
    browsers.put(OsBrowserType.Safari, "Safari");
    browsers.put(OsBrowserType.Edge, "Microsoft Edge");
    return browsers;
  }

  public HashMap<OsBrowserType, String> initWindowsBrowserMap() {
    HashMap<OsBrowserType, String> browsers = new HashMap<OsBrowserType, String>();
    browsers.put(OsBrowserType.Chrome, "Google Chrome");
    browsers.put(OsBrowserType.Firefox, "Firefox");
    browsers.put(OsBrowserType.Edge, "Edge");
    return browsers;
  }

  public HashMap<OsBrowserType, String> initLinuxBrowserMap() {
    HashMap<OsBrowserType, String> browsers = new HashMap<OsBrowserType, String>();
    browsers.put(OsBrowserType.Chrome, "google-chrome");
    browsers.put(OsBrowserType.Firefox, "firefox");
    return browsers;
  }
}
