package com.testsigma.automator.actions.mobile.android.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import org.openqa.selenium.WebElement;

public class DisableSwitchAction extends MobileElementAction {
  private static final String FAILURE_NULL_VALUE = "The element corresponding to the locator <b>\"%s:%s\"</b> is not a Switch/checkbox type.";
  private static final String SUCCESS_ALREADY_DISABLED = "The element with locator <b>\"%s:%s\"</b> is already disabled. " +
    "Not performing any action.";
  private static final String SUCCESS_MESSAGE = "Successfully disabled/unchecked element with locator <b>\"%s:%s\"</b>.";

  @Override
  protected void execute() throws Exception {

    findElement();
    WebElement targetElement = getElement();
    String checkable = targetElement.getAttribute(CHECKABLE);
    if (checkable.equals(TRUE)) {
      String checked = targetElement.getAttribute(CHECKED);
      if (checked.equals(TRUE)) {
        targetElement.click();
      } else {
        setSuccessMessage(String.format(SUCCESS_ALREADY_DISABLED, getFindByType(), getLocatorValue()));
        return;
      }
    } else {
      throw new AutomatorException(String.format(FAILURE_NULL_VALUE, getFindByType(), getLocatorValue()));
    }
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }
}
