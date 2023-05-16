/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.tap;

import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.offset.PointOption;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;

import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

@Log4j2
public class MobileNativeTapCoordinatesSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Tap action on given coordinates performed successfully";

  @Override
  public void execute() throws Exception {
    String[] splitCoordiantes = getTestData().split(",");
    int x = Integer.parseInt(splitCoordiantes[0]);
    int y = Integer.parseInt(splitCoordiantes[1]);
    PointerInput pointer = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(pointer, 1)
            .addAction(pointer.createPointerMove(ofMillis(0), viewport(), x, y))
            .addAction(pointer.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(pointer, ofMillis(2)))
            .addAction(pointer.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NumberFormatException) {
      setErrorMessage(String.format("Invalid coordinates entered %s", getTestData()));
      setErrorCode(ErrorCodes.PRESS_INVALID_ARGUMENT);
    } else if (e instanceof InvalidArgumentException) {
      setErrorCode(ErrorCodes.PRESS_INVALID_OPERATION);
      String message = e.getMessage();
      if (message != null && message.toUpperCase().contains("(WARNING")) {
        setErrorMessage(message.substring(0, message.toUpperCase().indexOf("(WARNING")));
      } else {
        setErrorMessage(String.format("Please verify that the given coordinates(%s) are valid.", getTestData()));
      }
    }
  }
}
