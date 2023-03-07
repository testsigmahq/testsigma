/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions;

import com.testsigma.automator.exceptions.AutomatorException;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Log4j2
public abstract class DriverAction extends Action {

  @Setter
  private WebDriver driver;

  protected WebDriver getDriver() {
    return this.driver;
  }

  protected RemoteWebDriver getRemoteWebDriver() {
    return (RemoteWebDriver) this.driver;
  }

  protected void handleException(Exception e) {
    super.handleException(e);
  }

  @Override
  protected void beforeExecute() throws AutomatorException {
    super.beforeExecute();
    setImplicitTimeout();
  }

  @Override
  protected void afterExecute() throws AutomatorException {
    super.afterExecute();
    resetImplicitTimeout();
  }

  private void setImplicitTimeout() {
    if (getGlobalElementTimeOut() != null && !getTimeout().equals(getGlobalElementTimeOut())) {
      log.info("Updating implicit timeout to step level timeout:" + getTimeout());
      setDriverImplicitTimeout(getTimeout());
    }
  }

  protected void setDriverImplicitTimeout(Duration timeInSeconds) {
    getDriver().manage().timeouts().implicitlyWait(timeInSeconds);
  }

  private void resetImplicitTimeout() {
    if (getGlobalElementTimeOut() != null && getTimeout().getSeconds() != getGlobalElementTimeOut()) {
      log.info("Resetting implicit timeout to Test plan level timeout:" + getGlobalElementTimeOut());
      setDriverImplicitTimeout(Duration.ofSeconds(getGlobalElementTimeOut()));
    }
  }

}
