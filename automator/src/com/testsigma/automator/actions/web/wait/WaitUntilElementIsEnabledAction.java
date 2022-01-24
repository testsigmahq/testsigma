package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

public class WaitUntilElementIsEnabledAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully waited till the given element is enabled on current page.";
  private static final String FAILURE_MESSAGE = "Element with locator <b>\"%s:%s\"</b> is not enabled," +
    " Waited for <b>%s</b> seconds for element to go into enabled state.";

  @Override
  public void execute() throws Exception {
    try {
      boolean elementDisabled = getWebDriverWait().until(CustomExpectedConditions.elementIsEnabled(getBy()));
      Assert.isTrue(elementDisabled, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()), (Exception) e.getCause());
    }
  }

}
