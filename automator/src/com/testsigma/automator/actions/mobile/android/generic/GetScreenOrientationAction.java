package com.testsigma.automator.actions.mobile.android.generic;


public class GetScreenOrientationAction extends com.testsigma.automator.actions.mobile.generic.GetScreenOrientationAction {

  public void getOrientation() {
    setActualValue(getDriver().getOrientation());
  }

}

