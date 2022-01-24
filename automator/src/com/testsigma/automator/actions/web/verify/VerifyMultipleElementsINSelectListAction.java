package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class VerifyMultipleElementsINSelectListAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that the select list with locator \"%s:%s\" " +
    "contain options with given text <b>\"%s\"</b>.";
  private static final String FAILURE_MESSAGE = "The select list with locator <b>\"%s:%s\"</b> is missing at least one of the options with given text <br> test data:\"%s\".";

  @Override
  protected void execute() throws Exception {
    List<String> notMatched = new ArrayList<>();
    List<String> allDataInList = new ArrayList<>();
    findElement();
    Select selectElement = new Select(getElement());
    String[] elements = getTestData().split(",");
    List<WebElement> options = selectElement.getOptions();
    for (WebElement we : options) {
      String optionText = we.getText();
      allDataInList.add(optionText);
    }
    for (String verifyText : elements) {
      if (!allDataInList.contains(verifyText)) {
        notMatched.add(verifyText);
      }
    }

    Assert.isTrue(notMatched.isEmpty(), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), notMatched));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue(), getTestDataMaskResult()));
  }
}
