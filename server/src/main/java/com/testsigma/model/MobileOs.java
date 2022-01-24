/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MobileOs {
  ANDROID,
  IOS;

  public Platform getPlatform() {
    if (this == ANDROID)
      return Platform.Android;
    else
      return Platform.iOS;
  }
}
