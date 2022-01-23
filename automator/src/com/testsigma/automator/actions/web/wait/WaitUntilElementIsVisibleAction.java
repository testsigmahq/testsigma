package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilElementIsVisibleAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully waited till the given element is visible on current page.";
  private static final String FAILURE_MESSAGE = "Element with locator <b>\"%s:%s\"</b> is not visible," +
    " Waited for <b>%s</b> seconds for element visibility.";

  @Override
  public void execute() throws Exception {
    try {
      WebElement visibleElement = getWebDriverWait().until(ExpectedConditions.visibilityOfElementLocated(getBy()));
      Assert.notNull(visibleElement, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()), (Exception) e.getCause());
    }
  }

}
