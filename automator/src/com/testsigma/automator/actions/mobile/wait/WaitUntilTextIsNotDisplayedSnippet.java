/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

@Log4j2
public class WaitUntilTextIsNotDisplayedSnippet extends MobileElementAction {


  private static final String SUCCESS_MESSAGE = "Waited until text is not displayed successfully";
  private static final String FAILURE_MESSAGE = "Fail to wait until text <b> %s </b> is not displayed";

  public static ExpectedCondition<Boolean> mobileTextToBePresent(final String text) {
    return new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        try {
          String elementText = driver.getPageSource();
          return elementText.contains(text);
        } catch (StaleElementReferenceException e) {
          return false; // return null is changed to return false// TODO::
        }
      }
    };
  }

  @Override
  public void execute() throws Exception {
    try {
      getWebDriverWait().until(ExpectedConditions.not(mobileTextToBePresent(getTestData())));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTestDataMaskResult()), (Exception) e.getCause());
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
