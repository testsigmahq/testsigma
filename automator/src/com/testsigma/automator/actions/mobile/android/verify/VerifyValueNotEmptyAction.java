package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.springframework.util.Assert;

public class VerifyValueNotEmptyAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "The value for element corresponding to the locator  \"%s:" +
    "%s\" has value \"%s\"";
  private static final String FAILURE_MESSAGE_MISMATCH = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\" </b>is empty";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is not available.";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(ActionConstants.ATTRIBUTE_VALUE));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    Assert.isTrue(!(getActualValue().toString().trim().isEmpty()), String.format(FAILURE_MESSAGE_MISMATCH,
      getFindByType(), getLocatorValue()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue(), getActualValue().toString()));
  }

}
