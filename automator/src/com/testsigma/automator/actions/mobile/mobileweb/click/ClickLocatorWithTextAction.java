package com.testsigma.automator.actions.mobile.mobileweb.click;

import com.testsigma.automator.actions.web.click.ClickOnElementUsingLocatorWithTextAction;

public class ClickLocatorWithTextAction extends ClickOnElementUsingLocatorWithTextAction {
  private static final String SUCCESS_MESSAGE = "Successfully executed Tap action.";

  @Override
  protected void execute() throws Exception {
    super.execute();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
