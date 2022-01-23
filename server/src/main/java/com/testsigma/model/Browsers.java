package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Browsers {
  GoogleChrome("GOOGLECHROME", "googlechrome", "chromedriver"),
  MozillaFirefox("FIREFOX", "mozilla", "geckodriver"),
  MicrosoftEdge("EDGE", "edge", "msedgedriver"),
  Safari("SAFARI", "", "");

  private final String key;
  private final String browserFolderName;
  private final String fileName;

  public static Browsers getBrowser(String key) {
    switch (key) {
      case "FIREFOX":
      case "MOZILLAFIREFOX":
      case "MozillaFirefox":
        return Browsers.MozillaFirefox;
      case "GOOGLECHROME":
      case "GoogleChrome":
      case "CHROME":
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
