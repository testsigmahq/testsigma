package com.testsigma.automator.actions.mobile.switchactions;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebDriverException;

import java.util.Set;

public class MobileNativeSwitchToContextWithNameAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully switched to context <b>\"%s\"</b>.<br>All Available contexts: <b>%s</b>";
  private static final String FAILURE_MESSAGE = "Context with name <b>\"%s\"</b> is not available.<br>Available context names:%s<br>Current Context:%s";

  Set<String> contexts = null;
  String currentContext = null;

  @Override
  protected void execute() throws Exception {
    currentContext = getDriver().getContext();
    contexts = getDriver().getContextHandles();
    getDriver().context(getTestData());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult(), contexts));
  }

  @Override
  public void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof WebDriverException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, getTestDataMaskResult(), contexts, currentContext));
    }
  }
}
