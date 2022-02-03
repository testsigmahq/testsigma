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
  Chrome("GoogleChrome", "CHROME"),
  Firefox("MozillaFirefox", "FIREFOX"),
  Safari("Safari", "SAFARI"),
  Edge("MicrosoftEdge", "EDGE"),
  Unknown("Unknown", "Unknown");

  @Getter
  @Setter
  private String browserName;
  @Getter
  @Setter
  private String hybridName;

  OSBrowserType(String name, String hybridName) {
    this.browserName = name;
    this.hybridName = hybridName;
  }

  public static Browsers getBrowser(String key) {
    for (OSBrowserType btype : OSBrowserType.values()) {
      if (btype.getHybridName().equals(key)) {
        return Browsers.valueOf(btype.browserName);
      }
    }
    return null;
  }

}
