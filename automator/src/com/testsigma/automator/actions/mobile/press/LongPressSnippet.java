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
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;

import java.time.Duration;

@Log4j2
public class LongPressSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Long press on element successful.";

  @Override
  public void execute() throws Exception {
    TouchAction action = new TouchAction(getDriver());
    findElement();
    WebElement targetElement = getElement();
    Duration duration = Duration.ofSeconds(Long.parseLong(getTestData()));
    action.press(PointOption.point(targetElement.getLocation())).waitAction(WaitOptions.waitOptions(duration)).release().perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
