/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.press;

import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;

@Log4j2
public class PressKeySnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Pressed key successfully";
  private static final String FAILURE_MESSAGE = "Keyboard is not opened, So unable to tap on <b>%s</b> key, please ensure keyboard is opened.";

  @Override
  public void execute() throws Exception {

    getDriver().getKeyboard().pressKey(Keys.valueOf(getTestData().toUpperCase()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof IllegalArgumentException) {
      setErrorMessage(String.format("Unable to press the key <b>%s</b>, given key in test data is not supported.", getTestDataMaskResult()));
      setErrorCode(ErrorCodes.PRESS_INVALID_ARGUMENT);
    } else if (e instanceof InvalidElementStateException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, getTestDataMaskResult()));
    }
  }
}
