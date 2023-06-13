package com.testsigma.automator.actions.mobile.android.generic;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.remote.DriverCommand;

public class ChangeScreenOrientationAction extends com.testsigma.automator.actions.mobile.generic.ChangeScreenOrientationAction {

  public void changeOrientation() {
    String orientation = getDriver().execute(DriverCommand.GET_SCREEN_ORIENTATION).getValue().toString();
    if (ScreenOrientation.LANDSCAPE.value().equalsIgnoreCase(orientation)) {
        changeToPortrait();
      } else if (ScreenOrientation.PORTRAIT.value().equalsIgnoreCase(orientation)){
        changeToLandscape();
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

