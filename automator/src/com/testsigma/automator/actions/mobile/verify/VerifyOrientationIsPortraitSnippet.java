/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.verify;

import com.testsigma.automator.actions.mobile.MobileDriverAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.ScreenOrientation;
import org.springframework.util.Assert;

@Log4j2
public class VerifyOrientationIsPortraitSnippet extends MobileDriverAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that the screen orientation is portrait.";
  private static final String SCREEN_NOT_PORTRAIT = "The screen orientation is not in Portrait mode.<br>" +
    "Current Orientation:%s";

  @Override
  public void execute() throws Exception {
    ScreenOrientation orientation = getDriver().getOrientation();
    Assert.isTrue(orientation.equals(ScreenOrientation.PORTRAIT), String.format(SCREEN_NOT_PORTRAIT, orientation));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
