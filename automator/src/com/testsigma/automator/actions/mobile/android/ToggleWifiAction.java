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
public class ToggleWifiAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Wifi toggled successfully";

  @Override
  public void execute() throws Exception {
    ((AndroidDriver) getDriver()).toggleWifi();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
