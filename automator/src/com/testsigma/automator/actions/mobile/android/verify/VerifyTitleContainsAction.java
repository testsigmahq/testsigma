package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyTitleContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified the title of current page";
  private static final String FAILURE_MESSAGE_MISMATCH = "The title for current page does not contains expected value." +
    "<br>Expected(to contain)=\"%s\"<br> Actual=\"%s\"";

  @Override
  protected void execute() throws Exception {
    setActualValue(getDriver().getTitle().trim());
    Assert.isTrue(getActualValue().toString().contains(getTestData().trim()), String.format(FAILURE_MESSAGE_MISMATCH,
      getTestData(), getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
