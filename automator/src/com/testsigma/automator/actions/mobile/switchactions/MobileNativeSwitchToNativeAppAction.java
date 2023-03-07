package com.testsigma.automator.actions.mobile.switchactions;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebDriverException;

import java.util.Set;

public class MobileNativeSwitchToNativeAppAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully switched to context <b>\"%s\"</b>.<br>All Available contexts: <b>%s</b>";
  private static final String FAILURE_MESSAGE = "Unable to switch to NATIVE_APP context. Please verify if the context is available." +
    "<br>Available context names:%s<br>Current Context:%s";

  Set<String> contexts = null;
  String currentContext = null;

  @Override
  protected void execute() throws Exception {
    currentContext = getCurrentContext();
    contexts = getContextHandles();
    context("NATIVE_APP");
    setSuccessMessage(String.format(SUCCESS_MESSAGE, "NATIVE_APP", contexts));
  }

  @Override
  public void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof WebDriverException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, contexts, currentContext));
    }
  }
}
