/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile;


import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.DriverAction;
import io.appium.java_client.AppiumDriver;

import io.appium.java_client.remote.MobileCapabilityType;
import lombok.extern.log4j.Log4j2;

import org.openqa.selenium.remote.Command;

import org.openqa.selenium.remote.Response;



@Log4j2
public abstract class MobileDriverAction extends DriverAction {

  @Override
  protected void beforeExecute() throws AutomatorException {
    super.beforeExecute();
    if (!AppiumDriver.class.isAssignableFrom(getDriver().getClass())) {
      throw new AutomatorException("Invalid appium driver found.");
    }
  }

  protected AppiumDriver getDriver() {
    return (AppiumDriver) super.getDriver();
  }

  public Boolean sessionActive() {
    try {
      Response response = getDriver().getCommandExecutor().execute(new Command(getDriver().getSessionId(), "status"));
      return (response.getStatus() == 0) ? Boolean.FALSE : Boolean.TRUE;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return Boolean.FALSE;
    }
  }

  protected void setTimeoutException() {
    if (sessionActive() == Boolean.FALSE) {
      setErrorMessage("Session expired.");
//      setErrorMessage("Session expired due to inactivity. No user actions in the last " + getCommandTimeoutValue() +
//        " seconds");
    }
  }

  protected String getCommandTimeoutValue() {
    String commandTimeout = "";
    try {
      commandTimeout = getDriver().getCapabilities().getCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT).toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return commandTimeout;
  }

}
