package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.actions.ElementAction;

public class SwitchToParentPageAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Switched to parent content.";

  @Override
  protected void execute() throws Exception {
    getDriver().switchTo().defaultContent();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
