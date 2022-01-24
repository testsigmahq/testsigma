package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

public class LongPressOnElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully pressed on the given element for <b>%s</b> seconds.";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data. Given test data <b>%s</b> is not a number.";

  @Override
  protected void execute() throws Exception {
    findElement();
    int noOfSeconds = NumberFormatter.getIntegerValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestDataMaskResult()));
    Duration time = Duration.ofSeconds(noOfSeconds);
    Actions actions = new Actions(getDriver());
    actions.clickAndHold(getElement()).pause(time).release().perform();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestDataMaskResult()));
  }
}
