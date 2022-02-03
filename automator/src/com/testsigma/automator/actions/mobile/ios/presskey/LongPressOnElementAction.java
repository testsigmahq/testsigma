package com.testsigma.automator.actions.mobile.ios.presskey;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.ElementOption;

import java.time.Duration;

public class LongPressOnElementAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully pressed on the given element for <b>%s</b> seconds.";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data. Given test data <b>%s</b> is not a number.";


  @Override
  protected void execute() throws Exception {
    findElement();
    TouchAction action = new TouchAction(getDriver());
    int noOfSeconds = NumberFormatter.getIntegerValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestDataMaskResult()));
    Duration time = Duration.ofSeconds(noOfSeconds);
    action.longPress(LongPressOptions.longPressOptions().withElement(ElementOption.element(getElement())).withDuration(time)).release().perform();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }
}
