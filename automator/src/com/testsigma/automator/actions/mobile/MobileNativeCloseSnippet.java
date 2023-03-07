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
public class MobileNativeCloseSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Closed App successfully";

  @Override
  public void execute() throws Exception {
    getDriver().execute(io.appium.java_client.MobileCommand.CLOSE_APP);
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
