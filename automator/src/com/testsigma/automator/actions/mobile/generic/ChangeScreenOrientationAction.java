package com.testsigma.automator.actions.mobile.generic;

import com.testsigma.automator.actions.mobile.MobileElementAction;

public abstract class ChangeScreenOrientationAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully Switched ScreenOrientation";

  @Override
  public void execute() throws Exception {
    changeOrientation();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  protected abstract void changeOrientation();

}

