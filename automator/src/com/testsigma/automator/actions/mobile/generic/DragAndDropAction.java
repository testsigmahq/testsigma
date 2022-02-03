package com.testsigma.automator.actions.mobile.generic;

import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.LongPressOptions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.ElementOption;
import org.openqa.selenium.WebElement;

import java.time.Duration;

public class DragAndDropAction extends MobileElementAction {
  @Override
  protected void execute() throws Exception {
    findElement(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_FROM_ELEMENT);
    WebElement targetElementFrom = getElement();
    findElement(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TO_ELEMENT);
    WebElement targetElementTo = getElement();
    TouchAction builder = new TouchAction(getDriver());
    TouchAction dragAndDrop = builder.longPress(LongPressOptions.longPressOptions()
        .withElement(ElementOption.element(targetElementFrom))).
      waitAction(WaitOptions.waitOptions(Duration.ofMillis(3000))).moveTo(ElementOption.element(targetElementTo)).release();
    dragAndDrop.perform();
    setSuccessMessage("Successfully executed drag and drop of element.");
  }

}
