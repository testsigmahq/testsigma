/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.automator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OsBrowserType {
  Chrome(1),
  Firefox(2),
  Safari(3),
  Edge(4),
  Unknown(5);

  private final Integer value;

  public static OsBrowserType getOsBrowserType(String browserName) {
    Browsers browser = Browsers.getBrowser(browserName);
    if (browser == Browsers.GoogleChrome) {
      return OsBrowserType.Chrome;
    } else if (browser == Browsers.MozillaFirefox) {
      return OsBrowserType.Firefox;
    } else if (browser == Browsers.MicrosoftEdge) {
      return OsBrowserType.Edge;
    } else if (browser == Browsers.Safari) {
      return OsBrowserType.Safari;
    }
    return OsBrowserType.Unknown;
  }

  public static Browsers getBrowserType(OsBrowserType browserName) {
    switch (browserName) {
      case Chrome:
        return Browsers.GoogleChrome;
      case Firefox:
        return Browsers.MozillaFirefox;
      case Edge:
        return Browsers.MicrosoftEdge;
      case Safari:
        return Browsers.Safari;
      default:
        return null;
    }
  }
}
