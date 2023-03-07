package com.testsigma.automator.actions.mobile.generic;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.time.Duration;
import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

public class DragAndDropAction extends MobileElementAction {
  @Override
  protected void execute() throws Exception {
    findElement(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_FROM_ELEMENT);
    WebElement targetElementFrom = getElement();
    findElement(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TO_ELEMENT);
    WebElement targetElementTo = getElement();
    int middleXCoordinate_dragElement = targetElementFrom.getLocation().x + (targetElementFrom.getSize().width / 2 );
    int middleYCoordinate_dragElement = targetElementFrom.getLocation().y + (targetElementFrom.getSize().height / 2 );
    int middleXCoordinate_dropElement = targetElementTo.getLocation().x + (targetElementTo.getSize().width / 2 );
    int middleYCoordinate_dropElement = targetElementTo.getLocation().x + (targetElementTo.getSize().height / 2 );
    PointerInput Finger = new PointerInput(PointerInput.Kind.TOUCH,"finger");
    Sequence swipe = new Sequence(Finger,1)
            .addAction(Finger.createPointerMove(ofMillis(0), viewport(), middleXCoordinate_dragElement, middleYCoordinate_dragElement))
            .addAction(Finger.createPointerDown(LEFT.asArg()))
            .addAction(Finger.createPointerMove(ofSeconds(5), viewport(), middleXCoordinate_dropElement, middleYCoordinate_dropElement))
            .addAction(Finger.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(swipe));

    setSuccessMessage("Successfully executed drag and drop of element.");
  }

}
