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
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

@Log4j2
public class MobileNativeTapSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Tap action performed successfully";

  @Override
  public void execute() throws Exception {
    findElement();
    PointerInput FINGER = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(FINGER, 1)
            .addAction(FINGER.createPointerMove(ofMillis(0), viewport(), getElement().getLocation().getX(), getElement().getLocation().getY()))
            .addAction(FINGER.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(FINGER, ofMillis(2)))
            .addAction(FINGER.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
