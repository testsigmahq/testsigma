package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class VerifyTextFromAllElementsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified.";
  private static final String FAILURE_MESSAGE = "Display text from any of the elements with locator <b>\"%s:%s\"</b> " +
    "is not matching with expected test data <b>\"%s\"</b>";

  @Override
  protected void execute() throws Exception {
    findElement();
    boolean expectedTextFound = false;
    String expectedText = getTestData();
    for (WebElement element : getElements()) {
      String actualText = element.getText();
      if (actualText != null && actualText.equals(expectedText)) {
        expectedTextFound = true;
        break;
      }
    }
    Assert.isTrue(expectedTextFound, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), expectedText));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
