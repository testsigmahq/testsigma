package com.testsigma.automator.actions.mobile.ios.generic;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.MobileBy;
import io.appium.java_client.ios.IOSDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;
@Log4j2
public class HideKeyboardAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Hide Keyboard executed successfully.";
  private static final String FAILURE_MESSAGE = "Unable to hide keyboard. Please try executing \"Tap on element\" outside keyboard.";

  @Override
  protected void execute() throws Exception {
    int i = 0;
    boolean keyboardShown = true;
    while (i < 5) {
      if (i == 0) {
        switchToActiveElementAndPressEnter();
      } else if (i == 1) {
        hideKeyboardByTappingOutsideKeyboard();
      } else if (i == 2) {
        clickOnReturnKeys();
      } else if (i == 3) {
        hideKeyboardByTappingOutsideKeyboard();
      } else {
        break;
      }
      i++;
      if (!isKeyboardShown()) {
        keyboardShown = false;
        break;
      }
    }
    Assert.isTrue(Boolean.FALSE.equals(keyboardShown), FAILURE_MESSAGE);
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  private void switchToActiveElementAndPressEnter() {
    try {
      getDriver().switchTo().activeElement().sendKeys(Keys.RETURN);
      log.info("Hide keyboard by switching to active element and clicking enter");
    } catch (Exception e) {
      log.error("Could not hide keyboard by switching to active element and clicking enter",e);
    }
  }

  private void hideKeyboardByTappingOutsideKeyboard() {
    String keyboardElementClassName = "XCUIElementTypeKeyboard";
    log.info("Using KEYBOARD ELEMENT(Tap/touch above keyboard) to hide keyboard, Element classname:" + keyboardElementClassName);

    try {
      WebElement element = getDriver().findElement(By.className(keyboardElementClassName));
      Point keyboardPoint =  element.getLocation();
      PointerInput finger = new PointerInput(TOUCH,"finger");
      Sequence tap = new Sequence(finger,1)
              .addAction(finger.createPointerMove(ofMillis(0), viewport(),keyboardPoint.getX() + 2, keyboardPoint.getY() - 2))
              .addAction(finger.createPointerDown(LEFT.asArg()))
              .addAction(new Pause(finger,ofMillis(1)))
              .addAction(finger.createPointerUp(LEFT.asArg()));
      getDriver().perform(Arrays.asList(tap));
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

  private void clickOnReturnKeys() {

    List.of("Return", "return", "done", "Done", "search", "Search", "Next", "next", "Go", "go").forEach(button -> {
              try {
                getDriver().findElement(By.xpath("//*[contains(@name, '" + button + "')]")).click();
              } catch (Exception e) {
                log.error("**Tried to hide keyboard by pressing return keys***", e);
              }
              try {
                getDriver().findElement(By.name(button));
              } catch (Exception e) {
                log.error("**Couldnt find the button name***"+":"+button, e);
              }
            }
    );
  }

  private void clickOnHideKeyBoardAccessibilityID() {
    log.info("Using Hide Keyboard accessibilityId to hide keyboard");
    try {
      getDriver().findElement(AppiumBy.ByAccessibilityId.accessibilityId("Hide keyboard")).click();
      log.info("Using Hide Keyboard accessibilityId to hide keyboard didn't throw any exception");
    } catch (Exception e) {
      log.error("**Tried to hide keyboard using Hide Keyboard id***", e);
    }

  }

  protected boolean isKeyboardShown() {
    setDriverImplicitTimeout(Duration.ofSeconds(5));
    boolean keyboardShown = ((IOSDriver) getDriver()).isKeyboardShown();
    resetImplicitTimeout();
    return keyboardShown;
  }

  protected void resetImplicitTimeout() {
    try {
      long webkitResponseTimeout = (Long) getDriver().getCapabilities().getCapability("webkitResponseTimeout");
      setDriverImplicitTimeout(Duration.ofSeconds(webkitResponseTimeout / 1000));
    } catch (Exception e) {
      setDriverImplicitTimeout(Duration.ofSeconds(getGlobalElementTimeOut()));
    }

  }


}
