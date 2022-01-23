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

import java.time.Duration;

@Log4j2
public class SwipeAction extends MobileDriverAction {

  @Getter
  @Setter
  private TapPoint[] tapPoints;

  @Override
  public void execute() throws Exception {
    PointOption startingPoint = PointOption.point(tapPoints[0].getX(), tapPoints[0].getY());
    PointOption endingPoint = PointOption.point(tapPoints[1].getX(), tapPoints[1].getY());
    new TouchAction<>(getDriver()).press(startingPoint)
      .waitAction(WaitOptions.waitOptions(Duration.ofSeconds(1))).moveTo(endingPoint).release().perform();
  }
}
