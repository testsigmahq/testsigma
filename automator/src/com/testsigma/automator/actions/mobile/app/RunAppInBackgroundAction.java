package com.testsigma.automator.actions.mobile.app;

import com.google.common.collect.ImmutableMap;
import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.MobileCommand;

import java.time.Duration;

public class RunAppInBackgroundAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully moved app to background for <b>%s</b> seconds.";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data. Given test data <b>%s</b> is not a number.";

  @Override
  protected void execute() throws Exception {
    int noOfSeconds = NumberFormatter.getIntegerValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestData()));
    Duration duration = Duration.ofSeconds(noOfSeconds);
    getDriver().execute(MobileCommand.RUN_APP_IN_BACKGROUND,ImmutableMap.of("seconds", (double)duration.toMillis() / 1000.0));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}

