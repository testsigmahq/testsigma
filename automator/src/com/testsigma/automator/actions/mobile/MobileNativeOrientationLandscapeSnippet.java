/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.ScreenOrientation;

@Log4j2
public class MobileNativeOrientationLandscapeSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Orientation set to landscape successfully";

  @Override
  public void execute() throws Exception {
    getDriver().rotate(ScreenOrientation.LANDSCAPE);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
