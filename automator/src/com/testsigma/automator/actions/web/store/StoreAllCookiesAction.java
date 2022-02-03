package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Cookie;

import java.util.Set;

public class StoreAllCookiesAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully saved all cookies in a run time variable.<br><b>%s=\"%s\"</b>.";
  private static final String SUCCESS_MESSAGE_EMPTY = "Cookies are not available.";

  @Override
  protected void execute() throws Exception {
    Set<Cookie> cookieSet = getDriver().manage().getCookies();
    if (!(cookieSet.isEmpty())) {
      runtimeDataProvider.storeRuntimeVariable(getTestData(), cookieSet.toString());
      resultMetadata.put(getTestData(), cookieSet);
    } else {
      setSuccessMessage(SUCCESS_MESSAGE_EMPTY);
      return;
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData(), cookieSet));
  }
}
