package com.testsigma.automator.actions.mobile.ios.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.openqa.selenium.By;
import org.springframework.util.Assert;

public class VerifyTextAbsentAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified. The current page does not display text \"%s\"";
  private static final String FAILURE_MESSAGE_MISMATCH = "The current page displays text <b>\"%s\"</b>, which is not expected.";

  @Override
  protected void execute() throws Exception {
    setActualValue(getDriver().findElement(By.tagName(ActionConstants.TAG_BODY)).getText().trim());
    Assert.isTrue(!(getActualValue().toString().contains(getTestData().trim())), String.format(FAILURE_MESSAGE_MISMATCH,
      getTestData()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
