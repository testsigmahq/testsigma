package com.testsigma.automator.actions.mobile.verify;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.springframework.util.Assert;

public class VerifyIfAppInstalledAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Verified that the given app is installed.";
  private static final String FAILURE_MESSAGE = "App with id <b>%s</b> is not installed in the device.";

  @Override
  protected void execute() throws Exception {
    boolean appInstalled = getDriver().isAppInstalled(getTestData());
    Assert.isTrue(appInstalled, String.format(FAILURE_MESSAGE, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
