package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.interactions.Actions;

public class DoubleClickOnElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully performed double click on element";

  @Override
  public void execute() throws Exception {
    findElement();
    Actions actions = new Actions(getDriver());
    actions.doubleClick(getElement()).build().perform();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
