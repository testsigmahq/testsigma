package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilPageTitleContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified page title.";
  private static final String FAILURE_MESSAGE = "Page title does not contains <b>\"%s\"</b>, If page is not loaded completely, you may try increasing " +
    " Waited <b>%s</b> seconds for page title to contain expected value.";

  @Override
  public void execute() throws Exception {
    try {
      boolean pageTitleValidated = getWebDriverWait().until(ExpectedConditions.titleContains(getTestData()));
      Assert.isTrue(pageTitleValidated, String.format(FAILURE_MESSAGE, getTestData(), getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestData(), getTimeout()), (Exception) e.getCause());
    }
  }
}
