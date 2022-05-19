/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.mobileweb.verify;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

@Log4j2
public class VerifyElementPresenceAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "The element corresponding to the locator \"%s:%s\"" +
    " is displayed in this page";
  private static final String FAILURE_MESSAGE = "The element corresponding to the locator <b>\"%s:%s\"</b>" +
    " is not displayed on the page";

  @Override
  public void execute() throws Exception {
    findElement();
    setActualValue(getElement().isDisplayed());
    Assert.isTrue(Boolean.TRUE.equals(getActualValue()), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }

}
