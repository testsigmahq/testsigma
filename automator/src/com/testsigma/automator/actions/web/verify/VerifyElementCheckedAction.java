package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyElementCheckedAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that the element is Checked.";
  private static final String FAILURE_MESSAGE = "The element corresponding to locator <b>\"%s:%s\"</b> is not in checked/selected state.";

  @Override
  public void execute() throws Exception {
    findElement();
    Assert.isTrue(getElement().isSelected(), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
