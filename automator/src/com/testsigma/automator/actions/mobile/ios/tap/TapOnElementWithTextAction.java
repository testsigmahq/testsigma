package com.testsigma.automator.actions.mobile.ios.tap;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

public class TapOnElementWithTextAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Tap on element executed successfully";

  @Override
  protected void execute() throws Exception {
    WebElement targetElement = getDriver().findElement(AppiumBy.accessibilityId(getTestData()));
    PointerInput Finger = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(Finger, 1)
            .addAction(Finger.createPointerMove(ofMillis(0), viewport(), targetElement.getLocation().getX(), targetElement.getLocation().getY()))
            .addAction(Finger.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(Finger, ofMillis(2)))
            .addAction(Finger.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
