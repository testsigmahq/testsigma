package com.testsigma.automator.actions.mobile.android.generic;

import org.openqa.selenium.ScreenOrientation;

public class ChangeScreenOrientationAction extends com.testsigma.automator.actions.mobile.generic.ChangeScreenOrientationAction {

  public void changeOrientation() {
    if (getDriver().getOrientation().equals(ScreenOrientation.LANDSCAPE)) {
      changeToPortrait();
    } else {
      changeToLandscape();
    }
  }

  public void changeToPortrait() {
    getDriver().rotate(ScreenOrientation.PORTRAIT);
  }

  public void changeToLandscape() {
    getDriver().rotate(ScreenOrientation.LANDSCAPE);
  }

}

