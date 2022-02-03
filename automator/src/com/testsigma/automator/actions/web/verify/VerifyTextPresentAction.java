package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.springframework.util.Assert;

@Log4j2
public class VerifyTextPresentAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified. The current page displays text \"%s\"";
  private static final String FAILURE_MESSAGE_MISMATCH = "The current page does not display text <b>\"%s\"</b>";

  @Override
  protected void execute() throws Exception {
    boolean textPresent = getDriver().findElement(By.tagName(ActionConstants.TAG_BODY)).getText().trim().contains(getTestData().trim());
    //In some cases, though element is displayed, isDisplayed() is false
    if (!textPresent) {
      String elementText = getPageTextUsingJavaScript();
      textPresent = elementText.contains(getTestData());
    }
    Assert.isTrue(textPresent, String.format(FAILURE_MESSAGE_MISMATCH,
      getTestData()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getTestData()));
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
