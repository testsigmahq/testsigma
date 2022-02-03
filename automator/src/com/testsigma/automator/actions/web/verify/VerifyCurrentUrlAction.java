package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyCurrentUrlAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified the current page URL.";
  private static final String FAILURE_MESSAGE = "The current page URL validation failed.Expected:\"%s\" <br>Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    setActualValue(getDriver().getCurrentUrl());
    Assert.isTrue(getTestData().trim().equalsIgnoreCase(getActualValue().toString().trim()),
      String.format(FAILURE_MESSAGE, getTestData(), getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
