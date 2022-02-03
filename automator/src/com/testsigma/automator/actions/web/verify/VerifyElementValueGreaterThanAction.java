package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyElementValueGreaterThanAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the element value is greater than given testdata";
  private static final String FAILURE_MESSAGE_INVALID_ACTUAL_VALUE = "The value of element corresponding to the locator <b>\"%s:%s\"<b/> " +
    "is not a Number. <br>Value of Element in page:%s";
  private static final String FAILURE_MESSAGE_INVALID_EXPECTED_VALUE = "The Expected(testdata) value is not a Number." +
    "Please update test step with a valid number, Ex: 23,45.8,3049 etc.";
  private static final String FAILURE_MESSAGE_MISMATCH = "The value of element corresponding to the locator <b>\"%s:%s\"<b/> " +
    "is not greater than given testdata . <br>Actual(Expected to be greater):%s<br>Expected:%s";

  @Override
  protected void execute() throws Exception {
    findElement();
    String elementText = getElement().getText();
    String elementValue = getElement().getAttribute("value");
    String actualValueString = elementText.isEmpty() ? elementValue : elementText;
    String stringWithOnlyNumber = actualValueString.replaceAll("[^0-9.]", "");
    if (stringWithOnlyNumber.length() > 0
      && !stringWithOnlyNumber.startsWith(".") && !stringWithOnlyNumber.endsWith(".")) {
      actualValueString = stringWithOnlyNumber;
    }
    Double actualValue = NumberFormatter.getDoubleValue(actualValueString, String.format(FAILURE_MESSAGE_INVALID_ACTUAL_VALUE,
      getFindByType(), getLocatorValue(), actualValueString));
    setActualValue(actualValue);
    Double expectedValue = NumberFormatter.getDoubleValue(getTestData(), FAILURE_MESSAGE_INVALID_EXPECTED_VALUE);
    Assert.isTrue((actualValue > expectedValue), String.format(FAILURE_MESSAGE_MISMATCH, getFindByType(), getLocatorValue(), actualValueString, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
