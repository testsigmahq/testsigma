/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.press;

import com.testsigma.automator.actions.mobile.MobileElementAction;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.Keys;

import java.util.List;

@Log4j2
public class PressEnterSnippet extends MobileElementAction {

  private static final String SUCCESS_MESSAGE = "Pressed enter key successfully.";
  private static final String FAILURE_MESSAGE = "Unable to tap on ENTER key due to unavailability of keyboard. please ensure keyboard is opened.";

  @Override
  public void execute() throws Exception {
    if (getDriver() instanceof AndroidDriver) {
      ((AndroidDriver) getDriver()).pressKey(new KeyEvent(AndroidKey.ENTER));
    } else {
      getDriver().findElement(AppiumBy.accessibilityId("Done")).click();
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }

  private void clickOnReturnKeys() {

    List.of("Return", "return", "done", "Done", "search", "Search", "Next", "next", "Go", "go").forEach(button -> {
              try {
                getDriver().findElement(By.xpath("//*[contains(@name, '" + button + "')]")).click();
              } catch (Exception e) {
                log.error(e, e);
              }
              try {
                getDriver().findElement(By.name(button));
              } catch (Exception e) {
                log.error(e, e);
              }
            }
    );
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof InvalidElementStateException) {
      setErrorMessage(FAILURE_MESSAGE);
    }
  }
}
