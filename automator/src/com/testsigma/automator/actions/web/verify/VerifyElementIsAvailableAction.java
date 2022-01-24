package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;

public class VerifyElementIsAvailableAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "The element corresponding to the locator \"%s:%s\"" +
    " is present in this page";

  @Override
  public void execute() throws Exception {
    findElement();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }
}
