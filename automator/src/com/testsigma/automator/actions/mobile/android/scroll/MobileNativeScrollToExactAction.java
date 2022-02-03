/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.scroll;

import com.testsigma.automator.actions.mobile.scroll.ScrollToCenterSnippet;
import io.appium.java_client.android.AndroidDriver;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.NotFoundException;

@Log4j2
public class MobileNativeScrollToExactAction extends ScrollToCenterSnippet {
  private static final String SUCCESS_MESSAGE = "scrolled to text successfully";
  private static final String FAILURE_MESSAGE = "Scroll cannot be executed due to unavailability of given text <b>\"%s\"</b> in current page." +
    " Please provide a valid text.";

  @Override
  public void execute() throws Exception {
    String uiSelector = "new UiSelector().textMatches(\"" + getTestData() + "\")";
    String command = "new UiScrollable(new UiSelector().scrollable(true).instance(0)).scrollIntoView("
      + uiSelector + ");";
    ((AndroidDriver) getDriver()).findElementByAndroidUIAutomator(command);
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NotFoundException) {
      setErrorMessage(String.format(FAILURE_MESSAGE, getTestData()));
    }
  }
}
