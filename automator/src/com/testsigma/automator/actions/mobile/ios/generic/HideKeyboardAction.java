package com.testsigma.automator.actions.mobile.ios.generic;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.springframework.util.Assert;

@Log4j2
public class HideKeyboardAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Hide Keyboard executed successfully.";
  private static final String FAILURE_MESSAGE = "Unable to hide keyboard. Please try executing \"Tap on element\" outside keyboard.";

  @Override
  protected void execute() throws Exception {
    IOSDriver driver = (IOSDriver) getDriver();
    int i = 0;
    while (driver.isKeyboardShown() && i < 5) {
      if (i == 0) {
        clickOnHideKeyBoardAccessibilityID();
      } else if (i == 1) {
        clickOnReturnToHideKeyboard();
      } else if (i == 2) {
        sendKeysToHideKeyboard(Keys.RETURN);
      } else if (i == 3) {
        hideKeyboardByTappingOutsideKeyboard();
      } else {
        break;
      }
      i++;
    }
    Assert.isTrue(Boolean.FALSE.equals(driver.isKeyboardShown()), FAILURE_MESSAGE);
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  private void hideKeyboardByTappingOutsideKeyboard() {
    String keyboardElementClassName = "XCUIElementTypeKeyboard";
    log.info("Using KEYBOARD ELEMENT(Tap/touch above keyboard) to hide keyboard, Element classname:" + keyboardElementClassName);

    try {
      IOSElement element = (IOSElement) getDriver().findElementByClassName(keyboardElementClassName);
      Point keyboardPoint = element.getLocation();
      TouchAction touchAction = new TouchAction(getDriver());
      touchAction.tap(PointOption.point(keyboardPoint.getX() + 2, keyboardPoint.getY() - 2)).perform();
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        log.error("Thread Interrupted exception, Ignore this", e);
      }
      log.info("Using KEYBOARD ELEMENT(Tap/touch above keyboard) to hide keyboard didn't throw any exception.");
    } catch (Exception e) {
      log.error("**Tried to hide keyboard using KEYBOARD ELEMENT(Tap/touch above keyboard)***", e);
    }
  }

  private void sendKeysToHideKeyboard(Keys aReturn) {

    log.info("Using SEND KEYS to hide keyboard, key sent:" + aReturn.toString());
    try {
      getDriver().getKeyboard().sendKeys(aReturn);
      log.info("Using SEND KEYS to hide keyboard didn't throw any exception,key sent:" + aReturn);
    } catch (Exception e) {
      log.error("**Tried to hide keyboard using SEND KEYS,Key sent:" + aReturn + " ***", e);
    }
  }

  private void clickOnReturnToHideKeyboard() {
    log.info("Using PRESS on return key to hide keyboard");
    try {
      getDriver().getKeyboard().pressKey(Keys.RETURN);
      log.info("Using PRESS on return key to hide keyboard didn't throw any exception");
    } catch (Exception e) {
      log.error("**Tried to hide keyboard using PRESS on return key ***", e);
    }
  }

  private void clickOnHideKeyBoardAccessibilityID() {
    log.info("Using Hide Keyboard accessibilityId to hide keyboard");
    try {
      getDriver().findElementByAccessibilityId("Hide keyboard").click();
      log.info("Using Hide Keyboard accessibilityId to hide keyboard didn't throw any exception");
    } catch (Exception e) {
      log.error("**Tried to hide keyboard using Hide Keyboard id***", e);
    }

  }
}
