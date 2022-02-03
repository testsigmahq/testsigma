/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.tap;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MobileNativeTapSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Tap action performed successfully";

  @Override
  public void execute() throws Exception {
    TouchAction action = new TouchAction(getDriver());
    findElement();
    action.tap(TapOptions.tapOptions().withElement(ElementOption.element(getElement()))).perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
