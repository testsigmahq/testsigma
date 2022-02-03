/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.ElementOption;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;

import java.time.Duration;

@Log4j2
public class MobileNativeLongPressSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Long Pressed successfully";

  @Override
  public void execute() throws Exception {
    TouchAction action = new TouchAction(getDriver());
    findElement();
    WebElement targetElement = getElement();
    Duration duration = Duration.ofSeconds(Long.parseLong(getTestData()));
    action.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(targetElement)).withDuration(duration)).release().perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
