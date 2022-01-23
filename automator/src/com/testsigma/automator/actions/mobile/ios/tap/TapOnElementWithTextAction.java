package com.testsigma.automator.actions.mobile.ios.tap;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.TapOptions;
import io.appium.java_client.touch.offset.ElementOption;
import org.openqa.selenium.WebElement;

public class TapOnElementWithTextAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Tap on element executed successfully";

  @Override
  protected void execute() throws Exception {
    TouchAction action = new TouchAction(getDriver());
    WebElement targetElement = getDriver().findElementByAccessibilityId(getTestData());
    action.tap(TapOptions.tapOptions().withElement(ElementOption.element(targetElement))).perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
