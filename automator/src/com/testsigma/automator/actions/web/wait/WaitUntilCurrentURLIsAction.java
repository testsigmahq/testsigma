package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilCurrentURLIsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until the current page URL is \"%s\"";
  private static final String FAILURE_MESSAGE = "The current page URL is not same as expected." +
    "Waited <b>%s</b> seconds for current page URL to change to expected value.<br>Expected URL:\"%s\"";

  @Override
  public void execute() throws Exception {
    try {
      boolean urlIsAsExpected = getWebDriverWait().until(ExpectedConditions.urlToBe(getTestData()));
      Assert.isTrue(urlIsAsExpected, String.format(FAILURE_MESSAGE, getTimeout(), getTestData()));
      setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout(), getTestData()), (Exception) e.getCause());
    }

  }
}
