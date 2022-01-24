package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.List;

public class WaitUntilElementsWithTagNameAreDisplayedAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully waited until all elements with given tag name are displayed";
  private static final String FAILURE_MESSAGE = "There are no elements matching tag name <b>\"%s\"</b> <br>or<br> Some/all of the elements with given tag name" +
    " are not displayed.";


  @Override
  public void execute() throws Exception {
    try {
      List<WebElement> elements = getWebDriverWait().until(CustomExpectedConditions.allElementsOfTagnameAreDisplayed(getTestData()));
      Assert.notNull(elements, String.format(FAILURE_MESSAGE, getTestDataMaskResult()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestDataMaskResult()), (Exception) e.getCause());
    }
  }

}
