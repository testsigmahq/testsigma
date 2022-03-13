package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilPageTitleIsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified page title.";
  private static final String FAILURE_MESSAGE = "Page title does not match with expected testdata , If page is not loaded completely, you may try increasing " +
    " Waited <b>%s</b> seconds for page title to match with <b>%s</b>.";

  @Override
  public void execute() throws Exception {
    try {
      boolean pageTitleValidated = getWebDriverWait().until(ExpectedConditions.titleIs(getTestData()));
      Assert.isTrue(pageTitleValidated, String.format(FAILURE_MESSAGE, getTimeout(), getTestData()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout(), getTestData()), (Exception) e.getCause());
    }
  }
}
