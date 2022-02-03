package com.testsigma.automator.actions.web.javascript;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public class ExecuteJavaScriptOnElementAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully executed given JavaScript";
  private static final String FAILURE_MESSAGE = "Unable to execute given JavaScript <br> <b>\"%s\"</b> <br> " +
    "Please verify the given Javascript is valid.<br>Ex Script: <b>arguments[0].value=testValue</b>,Where <b>arguments[0]</b> is the element identified with given element locator.";

  @Override
  protected void execute() throws Exception {

    findElement();
    WebElement element = getElement();
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    js.executeScript(getTestData(), element);
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof JavascriptException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, getTestData()));
      setErrorCode(ErrorCodes.JAVA_SCRIPT_EXCEPTION);
    }
  }
}
