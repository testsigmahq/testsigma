package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class VerifyValueEmptyAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "The value for element corresponding to the locator  \"%s:" +
    "%s\"  is Empty";
  private static final String FAILURE_MESSAGE_MISMATCH = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is NOT EMPTY <br>Actual value for element is:: \"%s\"";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The value property for element corresponding to the locator <b>\"%s:" +
    "%s\"</b> is not available.";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(ActionConstants.ATTRIBUTE_VALUE));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    Assert.isTrue(StringUtils.isBlank(getActualValue().toString()), String.format(FAILURE_MESSAGE_MISMATCH,
      getFindByType(), getLocatorValue(), getActualValue().toString()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }

}
