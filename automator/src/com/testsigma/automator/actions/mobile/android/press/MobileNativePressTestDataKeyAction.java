package com.testsigma.automator.actions.mobile.android.press;

import com.testsigma.automator.actions.mobile.press.PressKeySnippet;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;

public class MobileNativePressTestDataKeyAction extends PressKeySnippet {

  private static final String SUCCESS_MESSAGE = "Pressed key successfully";
  private static final String FAILURE_MESSAGE = "Keyboard is not opened, So unable to tap on <b>%s</b> key, please ensure keyboard is opened.";

  @Override
  public void execute() throws Exception {
    ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.valueOf(getTestData().toUpperCase())));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
