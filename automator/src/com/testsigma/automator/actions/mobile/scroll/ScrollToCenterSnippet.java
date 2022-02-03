/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.scroll;

import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

@Log4j2
public class ScrollToCenterSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully scrolled until given element is viewable.";

  @Override
  public void execute() throws Exception {
    findElement();
    WebElement targetElement = getElement();
    String scrollToElement = "arguments[0].scrollIntoView({ behavior: 'auto', block: 'center', inline: 'center'});";
    ((JavascriptExecutor) getDriver()).executeScript(scrollToElement, targetElement);
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof JavascriptException) {
      setErrorMessage(
        String.format("Unable to scroll given element into view. Please verify element with given locator <b>\"%s:%s\"</b> can " +
          "be scrolled into view.", getElementSearchCriteria().getFindByType(), getElementSearchCriteria().getByValue()));
      setErrorCode(ErrorCodes.PRESS_INVALID_ARGUMENT);
    }
  }
}
