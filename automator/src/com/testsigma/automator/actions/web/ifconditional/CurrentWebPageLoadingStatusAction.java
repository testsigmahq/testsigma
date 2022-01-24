package com.testsigma.automator.actions.web.ifconditional;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

@Log4j2
public class CurrentWebPageLoadingStatusAction extends ElementAction {
  private static final String LOADED_SUCCESS_MESSAGE = "Successfully the page is loaded completely.";
  private static final String LOADED_FAILURE_MESSAGE = "Page is not completely loaded in given wait time";
  private static final String NOT_LOADED_SUCCESS_MESSAGE = "Successfully the page is not completely loaded in given wait time.";
  private static final String NOT_LOADED_ERROR_MESSAGE = "Page is already loaded completely.";

  @Override
  public void execute() throws Exception {
    String status = getTestData();
    switch (status) {
      case ActionConstants.LOADED:
        try {
          boolean pageLoaded = getWebDriverWait().until(CustomExpectedConditions.waitForPageLoadUsingJS());
          Assert.isTrue(pageLoaded, LOADED_FAILURE_MESSAGE);
          setSuccessMessage(LOADED_SUCCESS_MESSAGE);
          break;
        } catch (TimeoutException e) {
          throw new AutomatorException(LOADED_FAILURE_MESSAGE, (Exception) e.getCause());
        }
      case ActionConstants.NOT_LOADED:
        try {
          boolean pageLoaded = getWebDriverWait().until(CustomExpectedConditions.waitForPageLoadUsingJS());
          Assert.isTrue(!pageLoaded, NOT_LOADED_ERROR_MESSAGE);
          setSuccessMessage(NOT_LOADED_SUCCESS_MESSAGE);
          break;
        } catch (TimeoutException e) {
          throw new AutomatorException(NOT_LOADED_ERROR_MESSAGE, (Exception) e.getCause());
        }
    }
  }
}
