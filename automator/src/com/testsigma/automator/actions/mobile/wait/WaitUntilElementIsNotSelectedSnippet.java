/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Log4j2
public class WaitUntilElementIsNotSelectedSnippet extends MobileElementAction {


  private static final String SUCCESS_MESSAGE = "Successfully waited until element is not selected";
  private static final String FAILURE_MESSAGE = "Fail to wait until element with locator <b>\"%s:%s\"</b> is not selected. " +
    "If expected behaviour is yet to happen in given timeout, try increasing timeout.";

  @Override
  public void execute() throws Exception {
    findElement();
    try {
      getWebDriverWait().until(ExpectedConditions.attributeContains(getElement(), "selected", "false"));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()), (Exception) e.getCause());
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
