/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Log4j2
public class MobileNativeWaitUntilAlertIsNotVisibleAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Waited until alert not visible successfully";
  private static final String FAILURE_MESSAGE = "An Alert with given element is still present, waited %s seconds.";

  @Override
  public void execute() throws Exception {
    try {
      getWebDriverWait().until(ExpectedConditions.invisibilityOfElementLocated(getBy()));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout()), (Exception) e.getCause());
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
