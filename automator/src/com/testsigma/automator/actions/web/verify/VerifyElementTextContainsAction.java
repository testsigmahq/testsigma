package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;


public class VerifyElementTextContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified display text.";
  private static final String FAILURE_MESSAGE = "Display text of element corresponding to locator <b>\"%s:%s\"</b> does not contain expected test data," +
    "<br>Expected: \"%s\"<br> Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    String expectedText = getTestData();
    setActualValue(getElement().getText());
    Assert.isTrue(getActualValue().toString().contains(expectedText), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(),
      expectedText, getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
