package com.testsigma.automator.actions.mobile.switchactions;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebDriverException;
import org.springframework.util.Assert;

import java.util.Set;

public class MobileNativeSwitchToWebviewAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully switched to context <b>\"%s\"</b>.<br>All Available contexts: <b>%s</b>";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "There is no webview context in this page. You may use " +
    "\"Switch context with name <CONTEXT_NAME>\" test step to switch to a desired context. <br>Available context names:%s<br>Current Context:%s";
  private static final String FAILURE_MESSAGE = "Current context is already a Webview context. You may use " +
    "\"Switch context with name <CONTEXT_NAME>\" test step to switch to a desired context.Below are the available contexts in this page.";
  Set<String> contexts = null;
  String currentContext = null;

  @Override
  protected void execute() throws Exception {
    currentContext = getDriver().getContext();
    String contextName = null;
    contexts = getDriver().getContextHandles();
    for (String context : contexts) {
      //WEBVIEW_Chrome is a chrome context, not a webview inside app.
      if (context != null && context.toUpperCase().startsWith("WEB") && !context.equalsIgnoreCase("WEBVIEW_chrome")) {
        contextName = context;
        break;
      }
    }
    Assert.notNull(contextName, String.format(FAILURE_MESSAGE_NOT_AVAILABLE, contexts, currentContext));
    Assert.isTrue(Boolean.FALSE.equals(currentContext.equalsIgnoreCase(contextName)), FAILURE_MESSAGE);
    getDriver().context(contextName);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, contextName, contexts));
  }

  @Override
  public void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof WebDriverException) {
      setErrorMessage(String.format(FAILURE_MESSAGE_NOT_AVAILABLE, contexts, currentContext));
    }
  }
}
