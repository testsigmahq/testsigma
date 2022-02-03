package com.testsigma.automator.actions.mobile.ios.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

public class WaitUntilTextDisplayedAction extends MobileElementAction {
  private final String SUCCESS_MESSAGE = "Successfully waited till the text gets displayed on current page.";
  private final String FAILURE_MESSAGE = "Text did not appear in the given wait time," +
    " Waited for <b>%s</b> seconds for text to appear.<br>Expected text(to display):\"%s\"";

  @Override
  public void execute() throws Exception {
    try {
      boolean textPresent = getWebDriverWait().until(CustomExpectedConditions.mobileTextToBePresent(getTestData()));
      Assert.isTrue(textPresent, String.format(FAILURE_MESSAGE, getTimeout(), getTestData()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout(), getTestData()), (Exception) e.getCause());
    }
  }
}
