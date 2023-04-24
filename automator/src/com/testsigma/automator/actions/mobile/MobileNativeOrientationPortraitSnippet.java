/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DriverCommand;

@Log4j2
public class MobileNativeOrientationPortraitSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Orientation set to portrait successfully";

  @Override
  public void execute() throws Exception {
    getDriver().execute(DriverCommand.SET_SCREEN_ORIENTATION,
            ImmutableMap.of("orientation", ScreenOrientation.PORTRAIT.value().toUpperCase()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
