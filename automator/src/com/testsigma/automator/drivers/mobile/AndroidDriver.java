/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.drivers.mobile;

import com.testsigma.automator.exceptions.AutomatorException;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

@Log4j2
public class AndroidDriver extends MobileDriver {

  public AndroidDriver() {
    super();
  }

  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    remoteWebDriver = new io.appium.java_client.android.AndroidDriver(remoteServerURL, desiredCapabilities);
  }
}
