/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.alert;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.MobileElement;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextAlertAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Tapped on Alert Text successfully";
  private static final String FAILURE_MESSAGE = "Unable to find an Alert which has button with text <b>\"%s\"</b>, " +
    "please verify if the alert is present.";
  private static final String PARAMETERIZED_XPATH = "//android.widget.Button[@text='$PARAMETER']";

  @Override
  public void execute() throws Exception {
    constructElementWithDynamicXpath(PARAMETERIZED_XPATH);
    findElement();
    MobileElement targetElement = (MobileElement) getElement();
    targetElement.click();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    updateErrorMessageForDynamicLocatorTypes(e, String.format(FAILURE_MESSAGE, getTestData()));
  }
}
