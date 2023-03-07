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
public class MobileNativeSwipeLeftToRightSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Swiped from left to right successfully";

  @Override
  public void execute() throws Exception {

    Dimension size = getDriver().manage().window().getSize();
    int startx = (int) (size.width * 0.10);
    int endx = (int) (size.width * 0.90);
    int starty = (int) (size.height * 0.50);
    PointerInput FINGER = new PointerInput(TOUCH, "finger");
    Sequence swipe = new Sequence(FINGER, 1)
            .addAction(FINGER.createPointerMove(ofMillis(0), viewport(), startx, starty))
            .addAction(FINGER.createPointerDown(LEFT.asArg()))
            .addAction(FINGER.createPointerMove(ofSeconds(5), viewport(), endx, starty))
            .addAction(FINGER.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(swipe));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
