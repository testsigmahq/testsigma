package com.testsigma.automator.actions.mobile.mobileweb.click;


public class ClickOnElementAction extends com.testsigma.automator.actions.web.click.ClickOnElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed Tap action.";

  @Override
  protected void execute() throws Exception {
    super.execute();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
