package com.testsigma.automator.actions.web.javascript;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.util.Assert;

public class ExecuteJavaScriptAndVerifyOutputAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that the given JavaScript returned an expected value. <br>Expected: <b>\"%s\"</b> <br>Actual: <b>\"%s\"</b>.";
  private static final String FAILURE_MESSAGE = "Unable to execute given JavaScript, <br> <b>\"%s\"</b> <br>" +
    "Please verify the given Javascript is valid.<br>Ex Script: <b>return document.getElementById(\"someId\").value</b>";
  private static final String OUTPUT_NOT_MATCHED = "Javascript executed but the output is not same as expected.<br>Expected: <b>\"%s\"</b> <br>Actual: <b>\"%s\"</b>.";

  @Override
  protected void execute() throws Exception {

    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    Object obj = js.executeScript(getTestData());
    String str = (obj == null) ? "" : obj.toString();
    Assert.isTrue(str.contains(getAttribute()), String.format(OUTPUT_NOT_MATCHED, getAttribute(), str));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getAttribute(), str));
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
