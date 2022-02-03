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
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

@Log4j2
public class ScreenshotAction extends MobileDriverAction {

  private static final String SUCCESS_MESSAGE = "Successfully took a screenshot of the current screen";

  @Override
  public void execute() throws Exception {
    String screenshotData = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BASE64);
    setActualValue("data:image/jpg;base64, " + screenshotData);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
