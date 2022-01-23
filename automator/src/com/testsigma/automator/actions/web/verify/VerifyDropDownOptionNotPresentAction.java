package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class VerifyDropDownOptionNotPresentAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the Select options corresponding to locator \"%s:%s\" " +
    "does not contain given test data.";
  private static final String FAILURE_MESSAGE = "Select list corresponding to locator <b>\"%s:%s\"</b> contains option(s) provided in test data." +
    "<br>Unexpected options:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    List<String> invalidOptions = new ArrayList<>();
    Select selectElement = new Select(getElement());
    List<WebElement> options = selectElement.getOptions();
    String[] optionsToVerify = getTestData().split(ActionConstants.NEW_LINE_CHARACTER);
    for (WebElement optionElem : options) {
      String optionText = optionElem.getText();
      for (String verifyText : optionsToVerify) {
        if (optionText.trim().equals(verifyText.trim())) {
          invalidOptions.add(optionText);
          break;
        }
      }
      //Break outer loop if all elements are found
      if (invalidOptions.size() == optionsToVerify.length) {
        break;
      }
    }
    Assert.isTrue(invalidOptions.isEmpty(), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), invalidOptions));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NoSuchElementException) {
      String errorMsg = getErrorMessage();
      setErrorMessage(String.format("%s<br>%s", errorMsg, ActionConstants.SELECT_OPTIONS_NOT_AVAILABLE));
    }
  }
}
