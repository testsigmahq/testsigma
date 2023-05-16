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
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
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
public class SwipeAction extends MobileDriverAction {

  @Getter
  @Setter
  private TapPoint[] tapPoints;

  @Override
  public void execute() throws Exception {
    PointOption startingPoint = PointOption.point(tapPoints[0].getX(), tapPoints[0].getY());
    PointOption endingPoint = PointOption.point(tapPoints[1].getX(), tapPoints[1].getY());
    PointerInput pointer = new PointerInput(TOUCH, "finger");
    Sequence swipe = new Sequence(pointer, 1)
            .addAction(pointer.createPointerMove(ofMillis(0), viewport(), tapPoints[0].getX(), tapPoints[0].getY()))
            .addAction(pointer.createPointerDown(LEFT.asArg()))
            .addAction(pointer.createPointerMove(ofSeconds(5), viewport(), tapPoints[1].getX(), tapPoints[1].getY()))
            .addAction(pointer.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(swipe));
  }
}
