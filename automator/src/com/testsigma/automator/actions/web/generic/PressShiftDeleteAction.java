package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

public class PressShiftDeleteAction extends ElementAction {

  @Override
  protected void execute() throws Exception {
    Actions actions = new Actions(getDriver());
    actions.sendKeys(Keys.chord(Keys.SHIFT), Keys.DELETE).build().perform();
    setSuccessMessage("SHIFT+DELETE keys pressed successfully.");
  }
}
