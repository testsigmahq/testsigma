package com.testsigma.automator.actions.web.switchactions;

import com.testsigma.automator.actions.ElementAction;

public class SwitchToParentFrameAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully switched to parent frame.";

  @Override
  protected void execute() throws Exception {
    getDriver().switchTo().parentFrame();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof IllegalArgumentException) {
      String message = e.getMessage().replaceAll("<", "").replaceAll(">", "");
      setErrorMessage("Cannot switch to parent frame," + message);
    }
  }
}
