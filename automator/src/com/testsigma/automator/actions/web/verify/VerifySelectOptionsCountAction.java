package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.support.ui.Select;
import org.springframework.util.Assert;

public class VerifySelectOptionsCountAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified Select list options count.";
  private static final String FAILURE_MESSAGE = "Select/combobox/list options count does not match with expected value." +
    "<br>Actual Value:\"%s\"<br>Expected Value:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    Select selectElement = new Select(getElement());
    setActualValue(selectElement.getOptions().size());
    Assert.isTrue(getActualValue().toString().equals(getTestData()), String.format(FAILURE_MESSAGE,
      getActualValue().toString(), getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

}
