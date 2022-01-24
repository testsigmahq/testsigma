package com.testsigma.automator.actions.mobile.android.tap;

import com.google.common.collect.ImmutableMap;
import com.testsigma.automator.actions.mobile.MobileElementAction;

public class TapOnSearchInKeyboardAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully executed tap on search button in keyboard.";

  @Override
  public void execute() throws Exception {
    getDriver().executeScript("mobile: performEditorAction", ImmutableMap.of("action", "search"));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
