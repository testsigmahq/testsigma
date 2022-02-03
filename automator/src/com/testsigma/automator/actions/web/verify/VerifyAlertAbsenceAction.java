/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

@Log4j2
public class VerifyAlertAbsenceAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Alert absence verified on the page";
  private static final String FAILURE_MESSAGE = "There is an alert present on the current page";

  @Override
  public void execute() throws Exception {
    try {
      Alert alert = getWebDriverWait().until(ExpectedConditions.alertIsPresent());
      Assert.isNull(alert, FAILURE_MESSAGE);
    } catch (TimeoutException | NotFoundException e) {
      setSuccessMessage(SUCCESS_MESSAGE);
    }
  }
}
