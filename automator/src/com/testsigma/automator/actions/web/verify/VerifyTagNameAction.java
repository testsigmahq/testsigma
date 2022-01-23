package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyTagNameAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified tag name.";
  private static final String FAILURE_MESSAGE = "Tag name of element corresponding to locator <b>\"%s:%s\"</b> does not match with expected test data," +
    "<br>Expected: \"%s\"<br> Actual:\"%s\"";
  private static final String FAILURE_NULL = "Tag name is not available for given element.<br>Actual tag name:%s";

  @Override
  protected void execute() throws Exception {
    findElement();
    String expectedText = getTestData();
    setActualValue(getElement().getTagName());
    Assert.notNull(getActualValue(), String.format(FAILURE_NULL, getActualValue()));
    Assert.isTrue(getActualValue().toString().equals(expectedText), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(),
      expectedText, getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
