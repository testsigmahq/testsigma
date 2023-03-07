package com.testsigma.automator.actions.mobile.ios.presskey;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.offset.ElementOption;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

public class LongPressOnElementAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully pressed on the given element for <b>%s</b> seconds.";
  private static final String FAILURE_NOT_A_NUMBER = "Please provide a valid number in test data. Given test data <b>%s</b> is not a number.";


  @Override
  protected void execute() throws Exception {
    findElement();
    WebElement targetElement = getElement();
    int noOfSeconds = NumberFormatter.getIntegerValue(getTestData(), String.format(FAILURE_NOT_A_NUMBER, getTestData()));
    Duration time = Duration.ofSeconds(noOfSeconds);
    PointerInput FINGER = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(FINGER, 1)
            .addAction(FINGER.createPointerMove(ofMillis(0), viewport(), targetElement.getLocation().getX(), targetElement.getLocation().getY()))
            .addAction(FINGER.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(FINGER, time))
            .addAction(FINGER.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
