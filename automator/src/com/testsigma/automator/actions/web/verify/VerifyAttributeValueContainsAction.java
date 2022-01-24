package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public class VerifyAttributeValueContainsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified attribute value. The attribute \"%s\" value for element corresponding to the specified locator " +
    "is: \"%s\"";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The attribute <b>\"%s\"</b> property for element corresponding to the locator <b>\"%s:%s\"</b> " +
    "is not available.";
  private static final String FAILURE_MESSAGE_MISMATCH = "The attribute <b>\"%s\"</b> property for element corresponding to the locator <b>\"%s:%s\"</b> " +
    "is not as expected.<br>Expected(to contain):\"%s\"<br>Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(getAttribute()));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getAttribute(), getFindByType(), getLocatorValue()));
    Assert.isTrue(getActualValue().toString().contains(getTestData()), String.format(FAILURE_MESSAGE_MISMATCH, getAttribute(),
      getFindByType(), getLocatorValue(), getTestDataMaskResult(), getActualValue()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getAttribute(), getActualValue()));
  }
}
