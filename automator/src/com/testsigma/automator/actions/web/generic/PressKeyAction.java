package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

public class PressKeyAction extends ElementAction {

  private static final String FAILURE_MESSAGE = "Unable to press the key <b>\"%s\"</b>.";

  @Override
  protected void execute() throws Exception {
    try {
      Actions actions = new Actions(getDriver());
      actions.sendKeys(Keys.valueOf(getTestData().toUpperCase().trim())).build().perform();
      setSuccessMessage("Successfully typed given test data.");
    } catch (Exception e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestDataMaskResult()));
    }

  }
}
