package com.testsigma.automator.actions.mobile.ios.generic;


public class GetScreenOrientationAction extends com.testsigma.automator.actions.mobile.generic.GetScreenOrientationAction {

  public void getOrientation() {
    setActualValue(getDriver().getOrientation());
  }

}

