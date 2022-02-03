package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;


public class WaitUntilElementHasValueAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until element has given value.";
  private static final String FAILURE_MESSAGE = "The element value corresponding to locator \"%s:%s\" is not matching with expected value." +
    "Waited %s seconds for element value to match with expected.<br>Expected value:%s";

  @Override
  protected void execute() throws Exception {
    try {
      boolean valueMatching = getWebDriverWait().until(ExpectedConditions.textToBePresentInElementValue(getBy(), getTestData()));
      Assert.isTrue(valueMatching, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout(), getTestData()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (
      TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout(), getTestData()), (Exception) e.getCause());
    }

  }

}
