package com.testsigma.automator.actions.mobile.enter;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.interactions.Actions;

public class EnterDataOnFocusedElement extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully entered data %s in Focused";

  @Override
  protected void execute() throws Exception {
    Actions a = new Actions(getDriver());
    a.sendKeys(getTestData());
    a.perform();
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
  }
}
