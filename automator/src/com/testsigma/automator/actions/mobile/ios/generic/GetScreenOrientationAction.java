package com.testsigma.automator.actions.mobile.ios.generic;


import org.openqa.selenium.remote.DriverCommand;

public class GetScreenOrientationAction extends com.testsigma.automator.actions.mobile.generic.GetScreenOrientationAction {

  public void getOrientation() {
    setActualValue(getDriver().execute(DriverCommand.GET_SCREEN_ORIENTATION).getValue().toString());
  }

}

