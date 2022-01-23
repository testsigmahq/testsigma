/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.drivers.mobile;

import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.entity.AppPathType;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.exceptions.AutomatorException;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

@Log4j2
public class IosDriver extends MobileDriver {

  public IosDriver() {
    super();
  }


  @Override
  protected void setCommonCapabilities() throws AutomatorException {
    super.setCommonCapabilities();
    capabilities.add(new WebDriverCapability(MobileCapabilityType.PLATFORM_NAME, Platform.iOS.name()));
  }

  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
    AppPathType appPathType = settings.getAppPathType();
    if (appPathType == AppPathType.APP_DETAILS) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.BUNDLE_ID, settings.getBundleId()));
    }
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    remoteWebDriver = new IOSDriver<>(getRemoteServerURL(), desiredCapabilities);
  }
}
