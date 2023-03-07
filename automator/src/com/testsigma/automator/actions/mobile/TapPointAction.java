/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import lombok.Getter;
import lombok.Setter;
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
public class TapPointAction extends MobileDriverAction {

  @Getter
  @Setter
  private TapPoint tapPoint;

  @Override
  public void execute() throws Exception {
    PointerInput FINGER = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(FINGER, 1)
            .addAction(FINGER.createPointerMove(ofMillis(0), viewport(), tapPoint.getX(), tapPoint.getY()))
            .addAction(FINGER.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(FINGER, ofMillis(2)))
            .addAction(FINGER.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
  }
}
