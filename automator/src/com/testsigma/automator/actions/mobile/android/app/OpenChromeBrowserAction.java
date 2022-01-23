package com.testsigma.automator.actions.mobile.android.app;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;

public class OpenChromeBrowserAction extends MobileElementAction {

  private final static String SUCCESS_MESSAGE = "Chrome Browser opened successfully.";

  @Override
  protected void execute() throws Exception {
    ((AndroidDriver) getDriver()).startActivity(new Activity("com.android.chrome", "com.google.android.apps.chrome.Main"));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
