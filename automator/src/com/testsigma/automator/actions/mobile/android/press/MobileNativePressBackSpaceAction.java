/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.press;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MobileNativePressBackSpaceAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Pressed Back Space successfully";

  @Override
  public void execute() throws Exception {
    ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.DEL));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
