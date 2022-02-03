package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyCurrentPageURLContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the current page URL contains \"%s\".";
  private static final String FAILURE_MESSAGE = "The current page URL does not contain expected value.<br>Expected:\"%s\" <br>Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    setActualValue(getDriver().getCurrentUrl());
    Assert.isTrue(getActualValue().toString().trim().toUpperCase().contains(getTestData().trim().toUpperCase()),
      String.format(FAILURE_MESSAGE, getTestDataMaskResult(), getActualValue().toString()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }
}
