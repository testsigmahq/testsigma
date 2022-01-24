/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Dimension;

@EqualsAndHashCode(callSuper = true)
@Log4j2
public class ScreenDimensionsAction extends MobileDriverAction {

  private static final String SUCCESS_MESSAGE = "Successfully fetched dimensions of the current screen";

  @Override
  public void execute() throws Exception {
    Dimension dimension = getDriver().manage().window().getSize();
    setActualValue(dimension);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}

