package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Alert;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilAlertPresentAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Alert presence verified on the page";
  private static final String FAILURE_MESSAGE = "There is no alert present on the current page," +
    " Waited for <b>%s</b> seconds for alert to appear.";

  @Override
  public void execute() throws Exception {
    try {
      Alert alert = getWebDriverWait().until(ExpectedConditions.alertIsPresent());
      Assert.notNull(alert, String.format(FAILURE_MESSAGE, getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout()), (Exception) e.getCause());
    }
  }
}
