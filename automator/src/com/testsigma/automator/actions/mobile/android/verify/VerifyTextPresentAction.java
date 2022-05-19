package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.springframework.util.Assert;

public class VerifyTextPresentAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that Given text is present in the page.";
  private static final String FAILURE_MESSAGE = "Text <b>%s</b> is not present in the page";

  @Override
  protected void execute() throws Exception {
    String pageSource = getDriver().getPageSource();
    Assert.isTrue(pageSource.contains(getTestData()), String.format(FAILURE_MESSAGE, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
