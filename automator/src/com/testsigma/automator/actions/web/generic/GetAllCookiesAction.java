package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;

import java.util.Set;

@Log4j2
public class GetAllCookiesAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Bellow are the available cookies <br><b>\"%s\"</b>. ";

  @Override
  protected void execute() throws Exception {
    Set<Cookie> cookies = getDriver().manage().getCookies();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, (cookies == null) ? "" : cookies.toString()));
  }
}
