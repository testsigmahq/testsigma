package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

public class VerifySelectOptionValue extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified Select field's option value.";

  private static final String FAILURE_MESSAGE = "Selected value of select list corresponding to locator <b>\"%s:%s\"</b> does not match with expected value." +
    "<br>Actual Selected Value:\"%s\"<br>Expected Value:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    String selectedOptionValue = selectElement.getFirstSelectedOption().getAttribute(ActionConstants.ATTRIBUTE_VALUE);
    Assert.notNull(selectedOptionValue, "\"value\" attribute is not present for the selected option");
    Assert.isTrue(selectedOptionValue.equals(getTestData()), String.format(FAILURE_MESSAGE,
      getFindByType(), getLocatorValue(), selectedOptionValue, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NoSuchElementException) {
      String errorMsg = getErrorMessage();
      setErrorMessage(String.format("%s\n%s", errorMsg, ActionConstants.SELECTED_OPTION_ERROR_MESSAGE));
    }
  }
}
