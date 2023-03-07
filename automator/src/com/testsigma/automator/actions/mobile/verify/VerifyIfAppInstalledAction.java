package com.testsigma.automator.actions.mobile.verify;

import com.google.common.collect.ImmutableMap;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.MobileCommand;
import org.openqa.selenium.remote.Response;
import org.springframework.util.Assert;

public class VerifyIfAppInstalledAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Verified that the given app is installed.";
  private static final String FAILURE_MESSAGE = "App with id <b>%s</b> is not installed in the device.";

  @Override
  protected void execute() throws Exception {
    Response response = getDriver().execute("isAppInstalled", ImmutableMap.of("bundleId", getTestData()));
    boolean appInstalled = Boolean.parseBoolean(response.getValue().toString());
    Assert.isTrue(appInstalled, String.format(FAILURE_MESSAGE, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
