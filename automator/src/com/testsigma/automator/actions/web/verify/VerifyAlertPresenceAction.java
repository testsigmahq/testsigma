/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

@Log4j2
public class VerifyAlertPresenceAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Alert presence verified on the page";
  private static final String FAILURE_MESSAGE = "Could not find any alert on the current page";

  @Override
  public void execute() throws Exception {
    Alert alert = getWebDriverWait().until(ExpectedConditions.alertIsPresent());
    Assert.notNull(alert, FAILURE_MESSAGE);
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof TimeoutException) {
      setErrorMessage("Alert is not present in current page. If alert is yet to load, please try increasing test wait time.");
      setErrorCode(ErrorCodes.NO_ALERT_PRESENT_EXCEPTION);
    }
  }
}
