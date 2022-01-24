package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ScreenOrientation {
  LANDSCAPE("landscape"),
  PORTRAIT("portrait");

  private final String value;

  public String value() {
    return value;
  }
}
