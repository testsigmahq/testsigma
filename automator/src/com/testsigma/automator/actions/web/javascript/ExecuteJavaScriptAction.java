package com.testsigma.automator.actions.web.javascript;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;

public class ExecuteJavaScriptAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully executed given JavaScript";
  private static final String FAILURE_MESSAGE = "Unable to execute given JavaScript, <br> <b>\"%s\"</b> " +
    "Please verify the given Javascript is valid.<br>Ex Script: <b>document.getElementById(\"someId\").value=\"testValue\"</b>";

  @Override
  protected void execute() throws Exception {

    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    js.executeScript(getTestData());
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof JavascriptException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, getTestDataMaskResult()));
      setErrorCode(ErrorCodes.JAVA_SCRIPT_EXCEPTION);
    }
  }

}
