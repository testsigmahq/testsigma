package com.testsigma.automator.actions.mobile.switchactions;

import com.google.common.collect.ImmutableMap;
import com.testsigma.automator.actions.mobile.MobileDriverAction;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.Response;

import java.util.Set;

public class MobileNativeSwitchToContextWithNameAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully switched to context <b>\"%s\"</b>.<br>All Available contexts: <b>%s</b>";
  private static final String FAILURE_MESSAGE = "Context with name <b>\"%s\"</b> is not available.<br>Available context names:%s<br>Current Context:%s";

  Set<String> contexts = null;
  String currentContext = null;

  @Override
  protected void execute() throws Exception {
    currentContext = getCurrentContext();
    contexts = getContextHandles();
    if (contexts.size() < 2) {
      //In browserstack webviews will not be listed on first call.
      Thread.sleep(2000);
      contexts = getContextHandles();
    }
    contexts.remove("NATIVE_APP");
    contexts.remove("WEBVIEW_chrome");
    context(getTestData());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), contexts));
  }

  @Override
  public void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof WebDriverException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, getTestData(), contexts, currentContext));
    }
  }
}
