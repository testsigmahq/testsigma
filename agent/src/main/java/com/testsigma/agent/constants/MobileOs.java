/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MobileOs {
  ANDROID("Android", "UiAutomator2"),
  IOS("iOS", "XCUITest");

  @Getter
  private final String platformName;

  @Getter
  private final String automationName;
}
