package com.testsigma.automator.actions.mobile.ios.switchactions;

import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.MobileElement;
import org.springframework.util.Assert;

public class DisableSwitchAction extends MobileElementAction {
  private static final String FAILURE_NULL_VALUE = "The element corresponding to the locator <b>\"%s:%s\"</b> is not a Switch/checkbox type." +
    "<br>Given Element Type:%s";
  private static final String SUCCESS_ALREADY_DISABLED = "The element with locator <b>\"%s:%s\"</b> is already disabled. " +
    "Not performing any action.";
  private static final String SUCCESS_MESSAGE = "Successfully disabled/unchecked element with locator <b>\"%s:%s\"</b>.";

  @Override
  protected void execute() throws Exception {
    findElement();
    MobileElement targetElement = (MobileElement) getElement();
    String value = targetElement.getAttribute(ActionConstants.ATTRIBUTE_VALUE);
    Assert.notNull(value, String.format(FAILURE_NULL_VALUE, getFindByType(), getLocatorValue(), targetElement.getAttribute(ActionConstants.ATTRIBUTE_TYPE)));
    if (!value.equals("0")) {
      targetElement.click();
    } else {
      setSuccessMessage(String.format(SUCCESS_ALREADY_DISABLED, getFindByType(), getLocatorValue()));
      return;
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));

  }
}
