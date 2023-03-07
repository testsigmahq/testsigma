package com.testsigma.automator.actions.mobile.ios.generic;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DriverCommand;

public class ChangeScreenOrientationAction extends com.testsigma.automator.actions.mobile.generic.ChangeScreenOrientationAction {

  public void changeOrientation() {
    if (getDriver().execute(DriverCommand.GET_SCREEN_ORIENTATION).getValue().toString().equalsIgnoreCase(ScreenOrientation.LANDSCAPE.value())) {
      changeToPortrait();
    } else {
      changeToLandscape();
    }
  }

  public void changeToPortrait() {
    getDriver().execute(DriverCommand.SET_SCREEN_ORIENTATION, ImmutableMap.of("orientation",ScreenOrientation.PORTRAIT.value().toUpperCase()));
  }

  public void changeToLandscape() {
    getDriver().execute(DriverCommand.SET_SCREEN_ORIENTATION, ImmutableMap.of("orientation",ScreenOrientation.LANDSCAPE.value().toUpperCase()));
  }

}

