package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

public class VerifySelectedOptionAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified Selected options text.";
  private static final String FAILURE_MESSAGE = "Selected text of select list corresponding to locator <b>\"%s:%s\"</b> does not match with expected value." +
    "<br>Actual Selected Value:\"%s\"<br>Expected Value:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    String selectedOptionValue = selectElement.getFirstSelectedOption().getText();
    Assert.notNull(selectedOptionValue, "text is not present for the selected option");
    Assert.isTrue(selectedOptionValue.equals(getTestData()), String.format(FAILURE_MESSAGE,
      getFindByType(), getLocatorValue(), selectedOptionValue, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NoSuchElementException) {
      String errorMsg = getErrorMessage();
      setErrorMessage(String.format("%s<br>%s", errorMsg, ActionConstants.SELECTED_OPTION_ERROR_MESSAGE));
    }
  }
}
