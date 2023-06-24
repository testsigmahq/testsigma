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
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

@Log4j2
public class MobileNativeSwipeElementToRightSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Swiped on element to right successfully";

  @Override
  public void execute() throws Exception {
    findElement();
    Dimension size = getDriver().manage().window().getSize();
    WebElement targetElement = getElement();
    int startx = (int) (size.width * 0.90);
    int endy = targetElement.getLocation().getY();
    int x = targetElement.getLocation().getX();
    int y = targetElement.getLocation().getY();

    PointerInput pointer = new PointerInput(TOUCH, "finger");
    Sequence swipe = new Sequence(pointer, 1)
            .addAction(pointer.createPointerMove(ofMillis(0), viewport(), x, y))
            .addAction(pointer.createPointerDown(LEFT.asArg()))
            .addAction(pointer.createPointerMove(ofSeconds(5), viewport(), startx, endy))
            .addAction(pointer.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(swipe));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
