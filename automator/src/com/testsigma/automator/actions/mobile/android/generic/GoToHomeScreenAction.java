package com.testsigma.automator.actions.mobile.android.generic;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidKeyCode;

public class GoToHomeScreenAction extends com.testsigma.automator.actions.mobile.generic.GoToHomeScreenAction {

  public void pressHome() {
    ((AndroidDriver) getDriver()).pressKeyCode(AndroidKeyCode.HOME);
  }

}

