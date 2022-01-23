package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.actions.ElementAction;

public class SwitchToFrameElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully switched to frame.";

  @Override
  protected void execute() throws Exception {
    findElement();
    getDriver().switchTo().frame(getElement());
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
