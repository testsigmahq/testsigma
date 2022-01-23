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
public class MobileNativeWaitUntilAlertIsVisibleAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Waited alert is displayed successfully";
  private static final String FAILURE_MESSAGE = "Cannot find an alert with given element.Waited for %s seconds.";
  private static final String FAILURE_NO_ALERT = "Cannot find an alert in given time. If Alert is yet to load, please try increasing timeout.";

  @Override
  public void execute() throws Exception {
    try {
      getWebDriverWait().until(ExpectedConditions.visibilityOfElementLocated(getBy()));
      verifyAlertPresence(FAILURE_NO_ALERT);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout()), (Exception) e.getCause());
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
