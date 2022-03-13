package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;


public class VerifyElementsCountAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified elements count";
  private static final String FAILURE_MESSAGE = "The elements count corresponding to locator <b>\"%s:%s\"</b> is not matching " +
    " with expected value. <br>Expected:\"%s\" <br>Actual:\"%s\"";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data, given test data <b>%s</b> is not a number.";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(Double.valueOf(getElements().size()));
    Double expectedCount = NumberFormatter.getDoubleValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestData()));
    Assert.isTrue(expectedCount.equals(getActualValue()), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(),
      expectedCount, ((Double) getActualValue()).intValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
