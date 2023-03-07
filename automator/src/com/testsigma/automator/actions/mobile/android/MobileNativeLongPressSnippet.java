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
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

@Log4j2
public class MobileNativeLongPressSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Long Pressed successfully";

  @Override
  public void execute() throws Exception {
    findElement();
    WebElement targetElement = getElement();
    Duration duration = Duration.ofSeconds(Long.parseLong(getTestData()));
    PointerInput Finger = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(Finger, 1)
            .addAction(Finger.createPointerMove(ofMillis(0), viewport(), targetElement.getLocation().getX(), targetElement.getLocation().getY()))
            .addAction(Finger.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(Finger, duration))
            .addAction(Finger.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
