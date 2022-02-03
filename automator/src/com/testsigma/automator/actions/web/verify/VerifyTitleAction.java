package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyTitleAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified the title of current page";
  private static final String FAILURE_MESSAGE_MISMATCH = "The title for current page is not matching with expected value." +
    "<br>Expected=\"%s\"<br> Actual=\"%s\"";

  @Override
  protected void execute() throws Exception {
    setActualValue(getDriver().getTitle().trim());
    Assert.isTrue(getActualValue().toString().equals(getTestData().trim()), String.format(FAILURE_MESSAGE_MISMATCH,
      getTestData(), getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
