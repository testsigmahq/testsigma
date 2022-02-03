package com.testsigma.automator.actions.mobile.ios.app;

import com.testsigma.automator.actions.mobile.MobileElementAction;

public class OpenAppUsingBundleIDAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully opened app with bundle id <b>%s</b>";
  private static final String FAILURE_MESSAGE = "Unable to open app using bundle id <b>%s</b>.Please verify if " +
    "the app with given bundle id is installed.";

  @Override
  protected void execute() throws Exception {
    getDriver().activateApp(getTestData().trim());
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    setErrorMessage(String.format(FAILURE_MESSAGE, getTestDataMaskResult()) + "<br>" + getErrorMessage());
  }
}
