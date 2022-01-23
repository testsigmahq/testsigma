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
public class VerifyAlertTextAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully validated Alert text";
  private static final String FAILURE_MESSAGE = "The alert is found to be present on the current page but the text in" +
    " the alert is not same as expected.<br>Expected: \"%s\" <br>Actual:\"%s\"";
  private static final String FAILURE_MESSAGE_NULL = "Could not find any alert on current page";


  @Override
  public void execute() throws Exception {
    Alert alert = getWebDriverWait().until(ExpectedConditions.alertIsPresent());
    Assert.notNull(alert, FAILURE_MESSAGE_NULL);
    setActualValue(alert.getText());
    Assert.isTrue(getActualValue().toString().contains(getTestData()), String.format(FAILURE_MESSAGE, getTestDataMaskResult(), getActualValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof TimeoutException) {
      setErrorMessage("Alert is not present in current page. If alert is yet to load, please try increasing test wait time");
      setErrorCode(ErrorCodes.NO_ALERT_PRESENT_EXCEPTION);
    }
  }
}
