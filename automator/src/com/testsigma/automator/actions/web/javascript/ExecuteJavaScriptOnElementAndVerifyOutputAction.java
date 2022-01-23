package com.testsigma.automator.actions.web.javascript;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ErrorCodes;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class ExecuteJavaScriptOnElementAndVerifyOutputAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that the given JavaScript returned an expected value. <br>Expected: <b>\"%s\"</b> <br>Actual: <b>\"%s\"</b>.";
  private static final String OUTPUT_NOT_MATCHED = " JavaScript executed but the output is not same as expected.<br>Expected: <b>\"%s\"</b> <br>Actual: <b>\"%s\"</b> ";
  private static final String FAILURE_MESSAGE = "Unable to execute given javascript:<br> <b>\"%s\"</b> <br>" +
    "Please verify the given Javascript is valid.<br>Ex Script: <b>return arguments[0].value=testValue</b>,Where <b>arguments[0]</b> is the element identified with given element locator.";

  @Override
  protected void execute() throws Exception {

    findElement();
    WebElement element = getElement();
    JavascriptExecutor js = (JavascriptExecutor) getDriver();
    Object value = js.executeScript(getTestData(), element);
    String output = (value == null) ? "" : value.toString();
    Assert.isTrue(output.contains(getAttribute()), String.format(OUTPUT_NOT_MATCHED, getAttribute(), output));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getAttribute(), output));
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
