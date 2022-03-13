package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.springframework.util.Assert;

public class VerifyValueAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified value attribute. The value for element corresponding to the specified locator " +
    "is: \"%s\"";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is not available.";
  private static final String FAILURE_MESSAGE_MISMATCH = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is not as expected.<br>Expected:\"%s\"<br>Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(ActionConstants.ATTRIBUTE_VALUE));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    Assert.isTrue(getActualValue().toString().equals(getTestData()), String.format(FAILURE_MESSAGE_MISMATCH,
      getFindByType(), getLocatorValue(), getTestData(), getActualValue().toString()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getActualValue().toString()));
  }
}
