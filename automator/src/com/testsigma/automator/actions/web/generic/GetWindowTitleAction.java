package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;

public class GetWindowTitleAction extends ElementAction {
  private static final String SUCCESS_MESSAGE_WITH_DATA = "Current window title::\"<b>%s</b>\"";

  @Override
  protected void execute() throws Exception {
    setSuccessMessage(String.format(SUCCESS_MESSAGE_WITH_DATA, getDriver().getTitle()));
  }
}
