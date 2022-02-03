package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;

public class DeleteCookieByNameAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Deleted cookie.";
  private static final String FAILURE_NO_COOKIE = "Cookie with name <b>\"%s\"</b> is not present.";


  @Override
  public void execute() throws Exception {
    if (getDriver().manage().getCookieNamed(getTestData()) != null) {
      getDriver().manage().deleteCookieNamed(getTestData());
    } else {
      throw new AutomatorException(String.format(FAILURE_NO_COOKIE, getTestDataMaskResult()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
