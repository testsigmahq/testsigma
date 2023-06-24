package com.testsigma.automator.actions.mobile.android.scroll;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;


public class ScrollInsideAnElementFromMiddleToTopAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully scrolled inside an element from middle to top";

  @Override
  protected void execute() throws Exception {

    findElement();
    Rectangle rect = getElement().getRect();
    PointerInput Finger = new PointerInput(TOUCH, "finger");
    Sequence swipe = new Sequence(Finger, 1)
            .addAction(Finger.createPointerMove(ofMillis(0), viewport(), rect.x + rect.width / 2, rect.y + rect.height / 2))
            .addAction(Finger.createPointerDown(LEFT.asArg()))
            .addAction(Finger.createPointerMove(ofSeconds(5), viewport(), rect.x + rect.width / 2, rect.y))
            .addAction(Finger.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(swipe));
    setSuccessMessage(SUCCESS_MESSAGE);

  }

}
