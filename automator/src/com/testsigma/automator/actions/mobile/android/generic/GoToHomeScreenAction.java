package com.testsigma.automator.actions.mobile.android.generic;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public class GoToHomeScreenAction extends com.testsigma.automator.actions.mobile.generic.GoToHomeScreenAction {

  public void pressHome() {
    ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.HOME));
  }

}

