package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

import java.util.List;

public class WaitUntilElementsWithClassNameAreDisplayedAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully waited until all elements with given class name are displayed";
  private static final String FAILURE_MESSAGE = "There are no elements matching classname <b>\"%s\"</b> <br>or<br> Some/all of the elements with given class name" +
    " are not displayed.";


  @Override
  public void execute() throws Exception {
    try {
      List<WebElement> elements = getWebDriverWait().until(CustomExpectedConditions.allElementsOfClassNameAreDisplayed(getTestData()));
      Assert.notNull(elements, String.format(FAILURE_MESSAGE, getTestData()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestData()), (Exception) e.getCause());
    }
  }

}
