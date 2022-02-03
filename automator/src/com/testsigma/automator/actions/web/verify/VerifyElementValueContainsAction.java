package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.springframework.util.Assert;

public class VerifyElementValueContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "The value for element corresponding to the locator  \"%s:" +
    "%s\" contains text \"%s\"";
  private static final String FAILURE_MESSAGE_MISMATCH = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is does not contain expected value.<br> Actual value of element: \"%s\"<br>Expected To contain:\"%s\"";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is not available.";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(ActionConstants.ATTRIBUTE_VALUE));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    Assert.isTrue(getActualValue().toString().contains(getTestData()), String.format(FAILURE_MESSAGE_MISMATCH,
      getFindByType(), getLocatorValue(), getActualValue().toString(), getTestData()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue(), getTestData()));
  }
}
