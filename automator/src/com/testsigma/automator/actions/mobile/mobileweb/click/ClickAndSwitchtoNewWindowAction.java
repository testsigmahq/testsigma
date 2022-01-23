package com.testsigma.automator.actions.mobile.mobileweb.click;

import com.testsigma.automator.actions.web.click.ClickAndSwitchToNewWindowAction;

public class ClickAndSwitchtoNewWindowAction extends ClickAndSwitchToNewWindowAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed Tap action and switched to new window.";

  @Override
  protected void execute() throws Exception {
    super.execute();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
