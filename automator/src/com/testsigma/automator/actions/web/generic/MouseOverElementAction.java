package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.interactions.Actions;

public class MouseOverElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Mouse over to element completed successfully.";

  @Override
  protected void execute() throws Exception {
    findElement();
    Actions actions = new Actions(getDriver());
    actions.moveToElement(getElement()).build().perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
