package com.testsigma.automator.actions.mobile.generic;

import com.testsigma.automator.actions.mobile.MobileElementAction;

public abstract class GetScreenOrientationAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully Fetched Orientation";

  @Override
  public void execute() throws Exception {
    getOrientation();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  protected abstract void getOrientation();

}

