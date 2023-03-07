/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.press;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;

@Log4j2
public class PressBackSpaceSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Pressed backspace key successfully";
  private static final String FAILURE_MESSAGE = "Unable to tap on BACK SPACE key due to unavailability of keyboard. please ensure keyboard is opened.";

  @Override
  public void execute() throws Exception {
    if (getDriver() instanceof AndroidDriver) {
      ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.DEL));
    } else {
      getDriver().findElement(AppiumBy.accessibilityId("delete")).click();
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof InvalidElementStateException) {
      setErrorMessage(FAILURE_MESSAGE);
    }
  }
}
