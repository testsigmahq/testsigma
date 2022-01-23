/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.swipe;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Dimension;

import java.time.Duration;

@Log4j2
public class MobileNativeSwipeBottomToTopSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Swiped from bottom to top executed successfully";

  @Override
  public void execute() throws Exception {
    Dimension size = getDriver().manage().window().getSize();
    int startx = (int) (size.width * 0.50);
    int starty = (int) (size.height * 0.90);
    int endy = (int) (size.height * 0.10);
    TouchAction swipeTo = new TouchAction(getDriver());
    Duration d = Duration.ofSeconds(5);
    swipeTo.press(PointOption.point(startx, starty)).waitAction(WaitOptions.waitOptions(d)).moveTo(PointOption.point(startx, endy)).release().perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
