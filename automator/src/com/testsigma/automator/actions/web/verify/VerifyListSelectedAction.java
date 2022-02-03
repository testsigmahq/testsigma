package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

public class VerifyListSelectedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the list has an option selected.";
  private static final String FAILURE_MESSAGE = "The list corresponding to locator <b>\"%s:%s\"</b> does not have any selected option.";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    Assert.isTrue(selectElement.getAllSelectedOptions().size() > 0, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
