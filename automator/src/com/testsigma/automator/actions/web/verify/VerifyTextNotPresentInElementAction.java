package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;


public class VerifyTextNotPresentInElementAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified.";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "Display Text for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is not available.";
  private static final String FAILURE_MESSAGE_MISMATCH = "The display text for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> validation failed.<br>Not Expected:\"%s\"<br>Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getText());
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    Assert.isTrue(!(getActualValue().toString().equals(getTestData())), String.format(FAILURE_MESSAGE_MISMATCH,
      getFindByType(), getLocatorValue(), getTestData(), getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
