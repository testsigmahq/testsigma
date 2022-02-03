package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class CancelAlertAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Alert canceled";
  private static final String FAILURE_MESSAGE = "Could not find any alert on the current page";

  @Override
  public void execute() throws Exception {
    Alert alert = getWebDriverWait().until(ExpectedConditions.alertIsPresent());
    Assert.notNull(alert, FAILURE_MESSAGE);
    alert.dismiss();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof TimeoutException) {
      setErrorMessage("Unable to cancel Alert. If alert is yet to load, please try increasing test wait time.");
      setErrorCode(ErrorCodes.NO_ALERT_PRESENT_EXCEPTION);
    }
  }
}
