package com.testsigma.automator.actions.mobile.ios.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilTextIsAbsentAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited till the text disappears from the page.";
  private static final String FAILURE_MESSAGE = "Text is still present in the given wait time," +
    " Waited for <b>%s</b> seconds for text to disappear.<br>Expected text(Not to display):\"%s\"";

  @Override
  protected void execute() throws Exception {
    try {
      boolean textNotPresent = getWebDriverWait().until(ExpectedConditions.not(CustomExpectedConditions.mobileTextToBePresent(getTestData())));
      Assert.isTrue(textNotPresent, String.format(FAILURE_MESSAGE, getTimeout(), getTestData()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout(), getTestData()), (Exception) e.getCause());
    }
  }
}
