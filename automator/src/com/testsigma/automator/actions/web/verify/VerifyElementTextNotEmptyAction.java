package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyElementTextNotEmptyAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the element has non-empty text.";

  private static final String FAILURE_MESSAGE = "The element corresponding to locator <b>\"%s:%s\"</b> has empty text.";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getText().isEmpty());
    Assert.isTrue(Boolean.FALSE.equals(getActualValue()), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
