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

@Log4j2
public class TapPointAction extends MobileDriverAction {

  @Getter
  @Setter
  private TapPoint tapPoint;

  @Override
  public void execute() throws Exception {
    new TouchAction<>(getDriver()).tap(PointOption.point(tapPoint.getX(), tapPoint.getY())).release().perform();
  }
}
