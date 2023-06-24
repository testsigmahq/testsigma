/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.verify;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

@Log4j2
public class MobileNativeVerifyAlertAbsentWithElementAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that an Alert is present and it does not contain given element.";
  private static final String FAILURE_MESSAGE_NULL = "Could not find an alert on current page, hence cannot verify if given element is absent.";


  @Override
  public void execute() throws Exception {
    verifyAlertPresence(FAILURE_MESSAGE_NULL);
    try {
      findElement();
    } catch (NotFoundException e) {
      setSuccessMessage(SUCCESS_MESSAGE);
      return;
    }
    WebElement targetElement = (WebElement) getElement();
    Assert.isTrue(!targetElement.isDisplayed(),
      String.format("The Alert contains element with given locator %s:%s which is not expected.",
        getElementSearchCriteria().getFindByType(), getElementSearchCriteria().getByValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
