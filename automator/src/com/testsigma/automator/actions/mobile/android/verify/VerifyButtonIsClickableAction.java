package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyButtonIsClickableAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified the given element is clickable.";
  private static final String FAILURE_MESSAGE = "The element with locator <b>\"%s:%s\"</b> is not clickable. " +
    "The Element is in Disabled state.";

  @Override
  protected void execute() throws Exception {

    findElement();
    Assert.isTrue(getElement().isEnabled(), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
