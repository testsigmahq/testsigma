package com.testsigma.automator.actions.mobile.ios.verify;

import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.springframework.util.Assert;

public class VerifySwitchEnabledAction extends MobileElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the Switch is enabled.";
  private static final String FAILURE_NULL_VALUE = "The element corresponding to the locator <b>\"%s:%s\"</b> is not a Switch/checkbox type." +
    "<br>Given Element Type:%s";
  private static final String FAILURE_ENABLED = "The Switch element corresponding to the locator <b>\"%s:%s\"</b> is in disabled state.";

  @Override
  public void execute() throws Exception {
    findElement();
    String value = getElement().getAttribute(ActionConstants.ATTRIBUTE_VALUE);
    Assert.notNull(value, String.format(FAILURE_NULL_VALUE, getFindByType(), getLocatorValue(), getElement().getAttribute(ActionConstants.ATTRIBUTE_TYPE)));
    Assert.isTrue(Boolean.FALSE.equals(value.equals("0")), String.format(FAILURE_ENABLED, getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
