package com.testsigma.automator.actions.mobile.app;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.mobile.MobileElementAction;

import java.time.Duration;

public class RunAppInBackgroundAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully moved app to background for <b>%s</b> seconds.";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data. Given test data <b>%s</b> is not a number.";

  @Override
  protected void execute() throws Exception {
    int noOfSeconds = NumberFormatter.getIntegerValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestDataMaskResult()));
    Duration duration = Duration.ofSeconds(noOfSeconds);
    getDriver().runAppInBackground(duration);
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }
}

