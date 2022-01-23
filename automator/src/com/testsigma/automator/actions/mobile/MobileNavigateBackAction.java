/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class MobileNavigateBackAction extends MobileDriverAction {

  private static final String SUCCESS_MESSAGE = "Navigate back executed successfully";

  @Override
  public void execute() throws Exception {
    getDriver().navigate().back();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
