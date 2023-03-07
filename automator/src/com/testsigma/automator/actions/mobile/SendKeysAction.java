/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import io.appium.java_client.AppiumDriver;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SendKeysAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully sent keys \"%s\" to the element corresponding to "
    + " locator type \"%s\" and locator \"%s\"";

  @Override
  public void execute() throws Exception {
    AppiumDriver driver = getDriver();
    findElement();
    if (getContextHandles().size() > 1) {
      tapByElementCoOrdinates(getElement(), driver);
    }
    getElement().sendKeys(getTestData());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), getFindByType(), getLocatorValue()));
  }
}
