package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.formatters.NumberFormatter;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.springframework.util.Assert;

public class ClickOnCoordinatesRelativeToElement extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully clicked on given location.<br>Element's Location(x,y):%s , %s<br>" +
    "Click Location(x,y): %s , %s";
  private static final String FAILURE_MESSAGE = "Unable to click at given location. Please verify if click action can be performed on given location." +
    ".<br>Element's location(x,y):%s , %s<br>Click Location(x,y): %s , %s";
  private static final String FAILURE_NOT_A_NUMBER_X = "Please provide a valid width percentage (can be a decimal too) in test data, given width <b>%s</b> is not a number.";
  private static final String TEST_DATA_FORMAT = "Test data format: Percentage of width , percentage of height (Ex: 20,40)<br>If the elements' dimensions are 1000 * 400, and if u want to click" +
    " at position 200,100(Assuming elements top left is at 0,0) then the test data should be 20,25 (which means 20 percent of 1000 , 25 percent of 400).";
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

    findElement();
    Rectangle elementsRectangle = getElement().getRect();
    int elementsX = elementsRectangle.getX();
    int elementsY = elementsRectangle.getY();
    int width = elementsRectangle.getWidth();
    int height = elementsRectangle.getHeight();
    Double clickLocationX = (xPercent * width / 100);
    Double clickLocationY = (yPercent * height / 100);
    String browserName = getRemoteWebDriver().getCapabilities().getBrowserName();
    Actions actions = new Actions(getDriver());
    //In Geckodriver, initial position of element starts from the center, so we need to move it to top left.
    if (browserName != null && browserName.trim().equalsIgnoreCase("firefox")) {
      actions.moveToElement(getElement(), -width / 2, -height / 2);
    } else {
      actions.moveToElement(getElement(), 0, 0);
    }
    actions.moveByOffset(clickLocationX.intValue(), clickLocationY.intValue()).click().build().perform();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, elementsX, elementsY, width + elementsX, height + elementsY));
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof MoveTargetOutOfBoundsException) {
      setErrorMessage("Unable to click on given location.<br>" + e.getMessage());
    }
  }
}
