package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

@Log4j2
public class WaitUntilTextDisplayedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited till the text gets displayed on current page.";
  private static final String FAILURE_MESSAGE = "Text did not appear in the given wait time," +
    " Waited for <b>%s</b> seconds for text to appear.<br>Expected text(to display):\"%s\"";

  @Override
  public void execute() throws Exception {
    try {
      boolean textPresent = getWebDriverWait().until(CustomExpectedConditions.textToBePresent(getTestData()));
      //In some cases, though element is displayed, isDisplayed() is false
      if (!textPresent) {
        String elementText = getPageTextUsingJavaScript();
        textPresent = elementText.contains(getTestData());
      }
      Assert.isTrue(textPresent, String.format(FAILURE_MESSAGE, getTimeout(), getTestDataMaskResult()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout(), getTestDataMaskResult()), (Exception) e.getCause());
    }
  }

  private String getPageTextUsingJavaScript() {
    try {
      return ((JavascriptExecutor) getDriver()).executeScript("return document.getElementsByTagName('body')[0].innerText").toString();
    } catch (Exception e) {
      log.error("Javascript execution to fetch page text failed, ignoring this as it is a fall back approach", e);
    }
    return "";
  }
}
