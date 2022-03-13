package com.testsigma.automator.actions.web.enter;

import org.openqa.selenium.JavascriptExecutor;

public class EnterDataUsingJavascript extends EnterAction {
  private static final String SUCCESS_MESSAGE = "Successfully entered given test data.";

  @Override
  protected void execute() throws Exception {
    findElement();
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    String script = String.format("arguments[0].value='%s';", getTestData());
    js.executeScript(script, getElement());
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
