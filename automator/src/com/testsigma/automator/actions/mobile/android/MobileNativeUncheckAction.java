/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebElement;

@Log4j2
public class MobileNativeUncheckAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Unchecked checkbox successfully";
  private static final String ELEMENT_IS_NOT_CHECKABLE = "Given element <b>\"%s:%s\"</b> is not checkable";

  @Override
  public void execute() throws Exception {
    findElement();
    WebElement targetElement = getElement();
    String checkable = targetElement.getAttribute(CHECKABLE);
    String checked = targetElement.getAttribute(CHECKED);
    if (checkable.equals(TRUE)) {
      if (checked.equals(TRUE)) {
        targetElement.click();
      } else {
        log.warn("The target element state is already unchecked, hence no action performed to uncheck.");
      }
    } else {
      throw new AutomatorException(String.format(ELEMENT_IS_NOT_CHECKABLE,
        getElementSearchCriteria().getFindByType(), getElementSearchCriteria().getByValue()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof AutomatorException) {
      setErrorMessage(e.getMessage());
      setErrorCode(ErrorCodes.CHECK_BOX_VALIDATION);
    }
  }
}
