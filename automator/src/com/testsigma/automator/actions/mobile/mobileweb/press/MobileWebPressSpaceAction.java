/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.mobileweb.press;

import com.testsigma.automator.actions.mobile.press.PressSpaceSnippet;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.ios.IOSMobileCommandHelper;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Keys;

@Log4j2
public class MobileWebPressSpaceAction extends PressSpaceSnippet {

  private static final String SUCCESS_MESSAGE = "Pressed space successfully";

  @Override
  public void execute() throws Exception {
    if (getDriver() instanceof AndroidDriver) {
      ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.SPACE));
    } else {
      getDriver().findElement(AppiumBy.accessibilityId("space")).click();
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
