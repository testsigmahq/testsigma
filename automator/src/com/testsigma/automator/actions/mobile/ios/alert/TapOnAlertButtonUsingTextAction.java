package com.testsigma.automator.actions.mobile.ios.alert;

import com.testsigma.automator.actions.mobile.MobileElementAction;

public class TapOnAlertButtonUsingTextAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully tapped on button which has text <b>\"%s\"</b>.";
  private static final String FAILURE_MESSAGE = "There is no button present with text <b>\"%s\"</b>." +
    " Please verify if the alert is opened and contains a button with given text.";
  private static final String PARAMETERIZED_XPATH = "//XCUIElementTypeButton[@name='$PARAMETER']";

  @Override
  protected void execute() throws Exception {
    constructElementWithDynamicXpath(PARAMETERIZED_XPATH);
    findElement();
    getElement().click();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    updateErrorMessageForDynamicLocatorTypes(e, String.format(FAILURE_MESSAGE, getTestData()));
  }
}
