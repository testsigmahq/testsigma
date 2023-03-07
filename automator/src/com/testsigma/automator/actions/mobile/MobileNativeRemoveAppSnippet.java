/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MobileNativeRemoveAppSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Removed App successfully";

  @Override
  public void execute() throws Exception {
    if(getDriver() instanceof AndroidDriver)
      ((AndroidDriver)getDriver()).removeApp(getTestData());
    else
      ((IOSDriver)getDriver()).removeApp(getTestData());
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
