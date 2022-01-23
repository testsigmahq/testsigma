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
import io.appium.java_client.MobileElement;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

@Log4j2
public class MobileNativeVerifyAlertPresentWithElementAction extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully verified that an Alert is present and contains given element.";
  private static final String FAILURE_MESSAGE_NULL = "Could not find an alert on current page.";

  @Override
  public void execute() throws Exception {
    verifyAlertPresence(FAILURE_MESSAGE_NULL);
    findElement();
    MobileElement targetElement = (MobileElement) getElement();
    Assert.isTrue(targetElement.isDisplayed(),
      String.format("The Alert` with locator %s:%s is not in displayed on the page",
        getElementSearchCriteria().getFindByType(), getElementSearchCriteria().getByValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
