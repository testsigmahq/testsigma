package com.testsigma.automator.actions.web.click;

import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

public class ClickOnElementUsingLocatorWithTextAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully executed click action.";
  private static final String FAILURE_ELEMENT_WITH_TEXT_NOT_FOUND = "Element(s) found with given locator <b>\"%s:%s\"</b>, " +
    "but none of the elements has expected text <b>\"%s\"</b>.<br>Actual text from Elements(COMMA separated):%s<br>Expected text:%s";

  @Override
  protected void execute() throws Exception {
    findElement();
    StringBuffer sb = new StringBuffer();
    boolean clickPerformed = false;
    for (WebElement element : getElements()) {
      String text = element.getText();
      if (text.equals(getTestData())) {
        element.click();
        clickPerformed = true;
        break;
      }
      sb.append(text + ",");
    }
    String s = sb.toString();
    if (s.contains(",")) {
      s = s.substring(0, s.length() - 1);
    }
    Assert.isTrue(clickPerformed, String.format(FAILURE_ELEMENT_WITH_TEXT_NOT_FOUND, getFindByType(), getLocatorValue(),
      getTestData(), s, getTestData()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
