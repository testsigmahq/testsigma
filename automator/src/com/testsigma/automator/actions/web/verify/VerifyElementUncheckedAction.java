package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyElementUncheckedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the element is UnChecked.";

  private static final String FAILURE_MESSAGE = "The element corresponding to locator <b>\"%s:%s\"</b> is checked/selected.";

  @Override
  public void execute() throws Exception {
    findElement();
    Assert.isTrue(!(getElement().isSelected()), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
