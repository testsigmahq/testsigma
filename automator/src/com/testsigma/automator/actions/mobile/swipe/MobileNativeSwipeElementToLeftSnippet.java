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
import org.openqa.selenium.WebElement;

import java.time.Duration;

@Log4j2
public class MobileNativeSwipeElementToLeftSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Swiped on element to left successfully";

  @Override
  public void execute() throws Exception {
    findElement();
    Dimension size = getDriver().manage().window().getSize();
    WebElement targetElement = getElement();
    int startx = (int) (size.width * 0.0);
    int endy = targetElement.getLocation().getY();
    int x = targetElement.getLocation().getX();
    int y = targetElement.getLocation().getY();
    TouchAction swipeTo = new TouchAction(getDriver());
    Duration d = Duration.ofSeconds(5);
    swipeTo.press(PointOption.point(x, y)).waitAction(WaitOptions.waitOptions(d)).moveTo(PointOption.point(startx, endy)).release().perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
