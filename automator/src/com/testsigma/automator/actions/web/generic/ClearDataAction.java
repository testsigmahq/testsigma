package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;

public class ClearDataAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Cleared data in given element.";

  @Override
  public void execute() throws Exception {
    findElement();
    getElement().clear();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

}
