package com.testsigma.automator.actions.mobile.ios.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.List;

import static com.testsigma.automator.constants.NaturalTextActionConstants.TESTS_TEP_DATA_MAP_KEY_ELEMENT;

public class WaitUntilAllImagesAreLoadedAction extends MobileElementAction {
  private static final String FAILURE_MESSAGE = "Some/all images in the page are not loaded.<br>or<br>There are no images in the current page.";
  private final String SUCCESS_MESSAGE = "Successfully waited until all images in the page are loaded.";

  @Override
  protected void execute() throws Exception {
    try {
      constructElementWithDynamicXpath("//XCUIElementTypeImage", TESTS_TEP_DATA_MAP_KEY_ELEMENT, null,
        null, false);
      //ExpectedConditions.visibilityOfElements always fails ..bcz in IOS element.isDisplayed() for images is always returning false.
      List<WebElement> elements = getWebDriverWait().until(CustomExpectedConditions.allElementsAreEnabled(getBy()));
      Assert.notNull(elements, FAILURE_MESSAGE);
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (
      TimeoutException e) {
      throw new AutomatorException(FAILURE_MESSAGE, (Exception) e.getCause());
    }
  }
}
