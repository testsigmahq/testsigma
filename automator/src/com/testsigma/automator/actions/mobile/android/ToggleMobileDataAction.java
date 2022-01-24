/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ToggleMobileDataAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Mobile Data toggled successfully";

  @Override
  public void execute() throws Exception {
    ((AndroidDriver) getDriver()).toggleData();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
