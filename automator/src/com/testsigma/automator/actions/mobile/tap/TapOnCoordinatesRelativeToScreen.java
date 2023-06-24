package com.testsigma.automator.actions.mobile.tap;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.springframework.util.Assert;

import java.util.Arrays;

import static java.time.Duration.ofMillis;
import static org.openqa.selenium.interactions.PointerInput.Kind.TOUCH;
import static org.openqa.selenium.interactions.PointerInput.MouseButton.LEFT;
import static org.openqa.selenium.interactions.PointerInput.Origin.viewport;

public class TapOnCoordinatesRelativeToScreen extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully tapped at given location.<br>Window dimensions(width,height):%s , %s<br>" +
    "Tap Location(x,y): %s , %s";
  private static final String FAILURE_MESSAGE = "Unable to tap at given location. Please verify if tap action can be performed on given location." +
    ".<br>Window dimensions(width,height):%s , %s<br>Tap Location(x,y): %s , %s";
  private static final String FAILURE_NOT_A_NUMBER_X = "Please provide a valid width percentage (can be a decimal too) in test data, given width <b>%s</b> is not a number.";
  private static final String TEST_DATA_FORMAT = "Test data format: Percentage of width , percentage of height (Ex: 20,40)<br>If the window dimensions are 1000 * 400, and if u want to tap" +
    " at position 200,100 then the test data should be 20,25 (which means 20 percent of 1000 , 25 percent of 400).";
  private static final String FAILURE_NOT_A_NUMBER_Y = "Please provide a valid height percentage (can be a decimal too) in test data, given height <b>%s</b> is not a number.";
  private static final String FAILURE_NO_SEPARATOR = "Please provide valid test data, given test data <b>\"%s\"</b> is not valid.";


  @Override
  protected void execute() throws Exception {
    Assert.isTrue(getTestData().contains(","), String.format(FAILURE_NO_SEPARATOR + "<br>" + TEST_DATA_FORMAT, getTestData()));
    String[] coOrdinates = getTestData().trim().split(",");
    double xPercent = NumberFormatter.getDoubleValue(coOrdinates[0], String.format(FAILURE_NOT_A_NUMBER_X + "<br>" + TEST_DATA_FORMAT, coOrdinates[0]));
    double yPercent = NumberFormatter.getDoubleValue(coOrdinates[1], String.format(FAILURE_NOT_A_NUMBER_Y + "<br>" + TEST_DATA_FORMAT, coOrdinates[1]));
    Assert.isTrue(xPercent <= 100, String.format("Given width percentage <b>%s</b> is greater than 100, which is not a valid value.<br>" + TEST_DATA_FORMAT, coOrdinates[0]));
    Assert.isTrue(yPercent <= 100, String.format("Given height percentage <b>%s</b> is greater than 100, which is not a valid value.<br>" + TEST_DATA_FORMAT, coOrdinates[1]));

    Dimension screenDimension = getDriver().manage().window().getSize();
    Double clickLocationX = (xPercent * screenDimension.getWidth() / 100);
    Double clickLocationY = (yPercent * screenDimension.getHeight() / 100);
    PointerInput pointer = new PointerInput(TOUCH, "finger");
    Sequence tap = new Sequence(pointer, 1)
            .addAction(pointer.createPointerMove(ofMillis(0), viewport(), clickLocationX.intValue(), clickLocationY.intValue()))
            .addAction(pointer.createPointerDown(LEFT.asArg()))
            .addAction(new Pause(pointer, ofMillis(2)))
            .addAction(pointer.createPointerUp(LEFT.asArg()));
    getDriver().perform(Arrays.asList(tap));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, screenDimension.getWidth(), screenDimension.getHeight(), clickLocationX, clickLocationY));
  }
}
