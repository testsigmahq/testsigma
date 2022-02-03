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
public enum Browsers {
  GoogleChrome("GOOGLECHROME", "googlechrome"),
  MozillaFirefox("FIREFOX", "mozilla"),
  MicrosoftEdge("EDGE", "edge"),
  Safari("SAFARI", "safari");

  private final String key;
  private final String browserFolderName;

  public static Browsers getBrowser(String key) {
    switch (key) {
      case "FIREFOX":
      case "MOZILLAFIREFOX":
      case "MozillaFirefox":
        return Browsers.MozillaFirefox;
      case "GOOGLECHROME":
      case "GoogleChrome":
        return Browsers.GoogleChrome;
      case "SAFARI":
      case "Safari":
        return Browsers.Safari;
      case "EDGE":
      case "MICROSOFTEDGE":
      case "MicrosoftEdge":
        return Browsers.MicrosoftEdge;
    }
    return null;
  }
}
