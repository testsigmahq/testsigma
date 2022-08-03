/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Platform {
  Generic("Testsigma", ""),
  Windows("Windows", "Windows"),
  Mac("OS X", "macOS"),
  MACOSX("MACOSX", "MACOSX"),
  Linux("Linux", ""),
  Android("Android", ""),
  iOS("iOS", "");
  private final String os;
  private final String versionPrefix;

  public Boolean isMobile() {
    return this == Platform.Android || this == Platform.iOS;
  }
}
