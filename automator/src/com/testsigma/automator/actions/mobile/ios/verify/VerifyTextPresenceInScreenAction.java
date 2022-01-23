package com.testsigma.automator.actions.mobile.ios.verify;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.springframework.util.Assert;

public class VerifyTextPresenceInScreenAction extends MobileElementAction {
  private static final String FAILURE_MESSAGE = "Given text <b>\"%s\"</b>is not present in current page/screen.";
  private static final String SUCCESS_MESSAGE = "Given text is present in current page.";

  @Override
  protected void execute() throws Exception {
    String pageSource = getDriver().getPageSource();
    Assert.isTrue(pageSource.contains(getTestData()), String.format(FAILURE_MESSAGE, getTestDataMaskResult()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
