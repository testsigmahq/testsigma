/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.tap;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

@Log4j2
public class MobileNativeClickSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Tap action performed successfully";
  public final String ELEMENT_IS_DISABLED = "Unable to click on the element. Element is disabled";

  @Override
  public void execute() throws Exception {
    findElement();

    WebElement targetElement = getElement();
    try {
      if (!targetElement.isEnabled()) {
        throw new AutomatorException(ELEMENT_IS_DISABLED);
      }
      targetElement.click();
    } catch (StaleElementReferenceException staleException) {
      log.info("Encountered StaleElementReferenceException");
      handleStaleelementExecptionOnClickAction();
    }

    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof UnsupportedOperationException) {
      setErrorMessage(String.format("Unable click on search Keyboard. unsupported operation"));
      setErrorCode(ErrorCodes.PRESS_INVALID_OPERATION);
    }
  }
}
