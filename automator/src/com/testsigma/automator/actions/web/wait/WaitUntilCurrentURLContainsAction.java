package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilCurrentURLContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until the current page URL contains \"%s\"";
  private static final String FAILURE_MESSAGE = "The current page URL does not contain <b>\"%s\"</b>. " +
    "Waited for <b>%s</b> seconds for current page URL to contain expected value";

  @Override
  public void execute() throws Exception {
    try {
      boolean urlContainsExpectedData = getWebDriverWait().until(ExpectedConditions.urlContains(getTestData()));
      Assert.isTrue(urlContainsExpectedData, String.format(FAILURE_MESSAGE, getTestData(), getTimeout()));
      setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestData(), getTimeout()), (Exception) e.getCause());
    }

  }
}
