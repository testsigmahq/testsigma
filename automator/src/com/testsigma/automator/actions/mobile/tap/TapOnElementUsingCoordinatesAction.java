package com.testsigma.automator.actions.mobile.tap;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import org.openqa.selenium.Rectangle;

public class TapOnElementUsingCoordinatesAction extends MobileElementAction {

  public static final String SUCCESS_MESSAGE = "Successfully executed tap action on the element using it's coordinates.";
  public static final String FAILURE_MESSAGE = "Unable to click on the element <b>\"%s\"</b> using it's coordinates.";

  @Override
  protected void execute() throws Exception {

    findElement();
    Rectangle rect = getElement().getRect();
    TouchAction action = new TouchAction(getDriver());
    action.tap(PointOption.point(rect.x + rect.width / 2, rect.y + rect.height / 2)).perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    setErrorMessage(String.format(FAILURE_MESSAGE, getLocatorValue()));
  }

}
