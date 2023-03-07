package com.testsigma.automator.actions.mobile.ios.verify;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.Alert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.Assert;

import java.time.Duration;

public class VerifyAlertPresentAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified Alert's presence in current page.";
  private static final String ALERT_VERIFICATION_FAILURE = "There is no Alert in current page.";

  @Override
  public void execute() throws Exception {
    WebDriverWait waiter = new WebDriverWait(getDriver(), Duration.ofMillis(30));
    Alert alert = waiter.until(ExpectedConditions.alertIsPresent());
    Assert.isTrue(alert != null, ALERT_VERIFICATION_FAILURE);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
