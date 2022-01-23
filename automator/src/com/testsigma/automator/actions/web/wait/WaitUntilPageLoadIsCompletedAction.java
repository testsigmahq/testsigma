package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

@Log4j2
public class WaitUntilPageLoadIsCompletedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until the page is loaded completely.";
  private static final String FAILURE_MESSAGE = "Page is not completely loaded in given wait time, If page is not loaded completely, you may try increasing step timeout." +
    " Waited <b>%s</b> seconds for page to load completely.";

  @Override
  public void execute() throws Exception {
    try {
      boolean pageLoaded = getWebDriverWait().until(CustomExpectedConditions.waitForPageLoadUsingJS());
      Assert.isTrue(pageLoaded, String.format(FAILURE_MESSAGE, getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout()), (Exception) e.getCause());
    }
  }
}
