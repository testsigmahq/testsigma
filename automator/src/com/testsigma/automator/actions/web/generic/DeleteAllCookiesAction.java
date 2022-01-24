package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class DeleteAllCookiesAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "All Cookies deleted successfully";

  @Override
  public void execute() throws Exception {
    getDriver().manage().deleteAllCookies();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
