package com.testsigma.automator.actions.mobile.ios.generic;

import com.testsigma.automator.actions.mobile.MobileElementAction;

public class PickAnOptionFromPickerAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully selected given value from picker element.";

  @Override
  protected void execute() throws Exception {
    findElement();
    getElement().sendKeys(getTestData());
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
