package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyElementEnabledAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "The element corresponding to the locator \"%s:%s\"" +
    " is in Enabled state";
  private static final String FAILURE_MESSAGE = "The element corresponding to the locator <b>\"%s:%s\"</b> is not Enabled ";

  @Override
  public void execute() throws Exception {
    findElement();
    setActualValue(getElement().isEnabled());
    Assert.isTrue(Boolean.TRUE.equals(getActualValue()), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }
}
