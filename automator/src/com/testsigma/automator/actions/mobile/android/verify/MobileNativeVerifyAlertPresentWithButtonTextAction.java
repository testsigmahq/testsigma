/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

@Log4j2
public class MobileNativeVerifyAlertPresentWithButtonTextAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Verified alert is displayed with button text successfully";
  private static final String FAILURE_MESSAGE_NOT_DISPLAYED = "Button with text <b>\"%s\"</b> is not displayed on this page. " +
    "Please verify if an alert is present and contains a button with given test data.";
  private static final String FAILURE_NOT_FOUND = "There is no Button with text <b>\"%s\"</b> present in this page. Please verify" +
    " if an alert is present and contains a button with given test data.";
  private static final String FAILURE_MESSAGE_NULL = "Could not find an alert on current page.";
  private static final String PARAMETERIZED_XPATH = "//android.widget.Button[@text='$PARAMETER']";

  @Override
  public void execute() throws Exception {
    verifyAlertPresence(FAILURE_MESSAGE_NULL);
    constructElementWithDynamicXpath(PARAMETERIZED_XPATH);
    findElement();
    WebElement targetElement = (WebElement) getElement();
    Assert.isTrue(targetElement.isDisplayed(),
      String.format(FAILURE_MESSAGE_NOT_DISPLAYED, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    updateErrorMessageForDynamicLocatorTypes(e, String.format(FAILURE_NOT_FOUND, getTestData()));
  }
}
