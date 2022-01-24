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
public class ClearElementAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully cleared the text of the element corresponding to the " +
    "locator type %s and locator %s";

  @Override
  public void execute() throws Exception {
    AppiumDriver driver = getDriver();
    findElement();
    if (driver.getContextHandles().size() > 1) {
      tapByElementCoOrdinates(getElement(), driver);
    }
    getElement().clear();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }
}
