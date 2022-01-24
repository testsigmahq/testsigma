/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;

@Log4j2
public class MobileNativeVerifyUnCheckedAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Verified checkbox is unchecked successfully";
  private static final String ELEMENT_IS_NOT_CHECKBLE = "Given element is not checkable. Please check that given locator <b>\"%s:%s\"</b>" +
    " is pointing to a checkbox type element.";

  @Override
  public void execute() throws Exception {
    findElement();
    WebElement targetElement = getElement();
    String checkable = targetElement.getAttribute(CHECKABLE);
    String checked = targetElement.getAttribute(CHECKED);
    if (checkable.equals(TRUE)) {
      if (checked.equals(TRUE)) {
        throw new AutomatorException(String.format("The element corresponding to the locator <b>\"%s:%s\"</b> is not in " +
            "unchecked state",
          getElementSearchCriteria().getFindByType(), getElementSearchCriteria().getByValue()));
      } else {
        log.info("The target element state is already Unchecked, hence no action performed to check.");
      }
    } else {
      throw new AutomatorException(String.format(ELEMENT_IS_NOT_CHECKBLE, getFindByType(), getLocatorValue()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof AutomatorException) {
      setErrorCode(ErrorCodes.CHECK_BOX_VALIDATION);
    }
  }
}
