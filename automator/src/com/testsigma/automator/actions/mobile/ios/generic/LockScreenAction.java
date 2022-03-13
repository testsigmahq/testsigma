package com.testsigma.automator.actions.mobile.ios.generic;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.ios.IOSDriver;

import java.time.Duration;

public class LockScreenAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully locked screen for <b>%s</b> seconds";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data. Given test data <b>%s</b> is not a number.";

  @Override
  protected void execute() throws Exception {
    int noOfSeconds = NumberFormatter.getIntegerValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestData()));
    Duration time = Duration.ofSeconds(noOfSeconds);
    ((IOSDriver) getDriver()).lockDevice(time);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
