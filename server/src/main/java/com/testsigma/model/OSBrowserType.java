/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Getter;
import lombok.Setter;

public enum OSBrowserType {
  Chrome("GoogleChrome", "CHROME","Chrome"),
  Firefox("MozillaFirefox", "FIREFOX","Firefox"),
  Safari("Safari", "SAFARI", "Safari"),
  Edge("MicrosoftEdge", "EDGE", "Edge"),
  Unknown("Unknown", "Unknown", "Unknown");

  @Getter
  @Setter
  private String browserName;
  @Getter
  @Setter
  private String hybridName;
  @Getter
  @Setter
  private String gridName;

  OSBrowserType(String name, String hybridName, String gridName) {
    this.browserName = name;
    this.hybridName = hybridName;
    this.gridName = gridName;
  }

  public static Browsers getBrowser(String key) {
    for (OSBrowserType btype : OSBrowserType.values()) {
      if (btype.getHybridName().equals(key)) {
        return Browsers.valueOf(btype.browserName);
      }
    }
    return null;
  }

  public static Browsers getBrowserEnumValueIfExists(String key) {
    for (OSBrowserType btype : OSBrowserType.values()) {
      if (btype.getGridName().equals(key)) {
        return Browsers.valueOf(btype.browserName);
      }
    }
    return null;
  }

}
