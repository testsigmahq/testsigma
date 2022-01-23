package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

public class WaitUntilElementIsDisabledAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully waited till the given element is disabled on current page.";
  private static final String FAILURE_MESSAGE = "Element with locator <b>\"%s:%s\"</b> is not disabled," +
    " Waited for <b>%s</b> seconds for element to go into disabled state.";

  @Override
  public void execute() throws Exception {
    try {
      boolean elementDisabled = getWebDriverWait().until(CustomExpectedConditions.elementIsDisabled(getBy()));
      Assert.isTrue(elementDisabled, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()), (Exception) e.getCause());
    }
  }

}

