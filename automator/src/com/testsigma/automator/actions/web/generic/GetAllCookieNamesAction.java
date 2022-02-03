package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Log4j2
public class GetAllCookieNamesAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Below are the available cookie names <br><b>\"%s\"</b>. ";

  @Override
  protected void execute() throws Exception {
    List<String> names = new ArrayList<>();
    Set<Cookie> allCookies = getDriver().manage().getCookies();
    for (Cookie cookie : allCookies) {
      names.add(cookie.getName());
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, names));
  }
}
