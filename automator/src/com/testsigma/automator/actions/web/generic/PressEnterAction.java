package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

public class PressEnterAction extends ElementAction {
  @Override
  protected void execute() throws Exception {
    Actions actions = new Actions(getDriver());
    actions.sendKeys(Keys.ENTER).build().perform();
    setSuccessMessage("Enter key pressed Successfully ");
  }
}
