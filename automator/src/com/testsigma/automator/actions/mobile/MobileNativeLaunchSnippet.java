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
public class MobileNativeLaunchSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Launch app executed successfully";

  @Override
  public void execute() throws Exception {
    getDriver().launchApp();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
