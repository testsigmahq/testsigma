/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.common;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SendKeysAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully entered data \"%s\"";

  @Override
  public void execute() throws Exception {
    findElement();
    getElement().sendKeys(getTestData());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
