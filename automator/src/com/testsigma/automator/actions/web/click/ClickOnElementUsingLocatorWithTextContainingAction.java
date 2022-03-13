package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class ClickOnElementUsingLocatorWithTextContainingAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully executed click action.";
  private static final String FAILURE_MESSAGE = "Element(s) found with given locator <b>\"%s:%s\"</b>, " +
    "but none of the elements has text containing <b>\"%s\"</b>.<br>Actual text from Elements(COMMA separated):%s<br>Expected text to contain:%s";

  @Override
  protected void execute() throws Exception {

    findElement();
    StringBuffer stringBuffer = new StringBuffer();
    boolean clickElement = false;
    for (WebElement webElement : getElements()) {
      String text = webElement.getText();
      if (text.contains(getTestData())) {
        webElement.click();
        clickElement = true;
        break;
      }
      stringBuffer.append(text + ",");
    }
    String s = stringBuffer.toString();
    if (s.contains(",")) {
      s = s.substring(0, s.length() - 1);
    }
    Assert.isTrue(clickElement, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(),
      getTestData(), s, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
