package com.testsigma.automator.actions.web.store;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
public class StoreCookieNameAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully saved cookie name and value in a run time variable.<br>%s=<b>\"%s\"</b>.";
  private static final String FAILURE_MESSAGE = "There is no cookie with given name <b>\"%s\"</b>.<br> Available cookies name=\"%s\"";
  private static final String FAILURE_NO_COOKIES = "Cookies are not available.";

  @Override
  protected void execute() throws Exception {

    Cookie cookie = getDriver().manage().getCookieNamed(getTestData());
    Assert.notNull(cookie, String.format(FAILURE_MESSAGE, getTestData(),
      getAvailableCookieNames()));
    String nameAndValue = cookie.getName() + "=" + cookie.getValue();
    runtimeDataProvider.storeRuntimeVariable(getAttribute(), nameAndValue);
    resultMetadata.put(getAttribute(), nameAndValue);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getAttribute(), nameAndValue));
  }

  private List<String> getAvailableCookieNames() throws AutomatorException {
    List<String> cooKieName = new ArrayList<>();
    Set<Cookie> allCookies = getDriver().manage().getCookies();
    if (!allCookies.isEmpty()) {
      for (Cookie cookie : allCookies) {
        cooKieName.add(cookie.getName());
      }
    } else {
      throw new AutomatorException(FAILURE_NO_COOKIES);
    }
    return cooKieName;
  }
}
