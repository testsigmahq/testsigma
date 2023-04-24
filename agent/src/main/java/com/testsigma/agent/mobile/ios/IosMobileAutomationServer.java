/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile.ios;

import com.testsigma.agent.constants.MobileOs;
import com.testsigma.agent.exception.MobileAutomationServerSessionException;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.mobile.MobileAutomationServerService;
import com.testsigma.agent.mobile.DeviceContainer;
import com.testsigma.agent.mobile.MobileDevice;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URL;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class IosMobileAutomationServer {

  private final MobileAutomationServerService mobileAutomationServerService;
  private final DeviceContainer deviceContainer;

  public void installDrivers(String uniqueId) {

  }

  public void uninstallDrivers(String uniqueId) {

  }

  public void createSession(String uniqueId) throws MobileAutomationServerSessionException {
    try {
      MobileDevice device = deviceContainer.getDevice(uniqueId);

      log.info("Creating IosDriver Session on device - " + device.getUniqueId());
      DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
      desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, MobileOs.IOS.getPlatformName());
      desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_VERSION, device.getOsVersion());
      desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, MobileOs.IOS.getAutomationName());
      desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, device.getName());
      desiredCapabilities.setCapability(MobileCapabilityType.UDID, device.getUniqueId());
      desiredCapabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 3600);
      desiredCapabilities.setCapability("xcodeOrgId", "6F4CKCA4LX");
      desiredCapabilities.setCapability("xcodeSigningId", "iPhone Developer");
      desiredCapabilities.setCapability("app", "/Users/vikram/ios-apps/ios-test-app/build/Release-iphoneos/TestApp-iphoneos.app");
      device.setRemoteWebDriver(new IOSDriver(new URL(mobileAutomationServerService.getMobileAutomationServer().getServerURL()),
        desiredCapabilities));
    } catch (Exception e) {
      throw new MobileAutomationServerSessionException(e.getMessage());
    }
  }

  public void deleteSession(String uniqueId) throws TestsigmaException {
    MobileDevice device = deviceContainer.getDevice(uniqueId);
    RemoteWebDriver remoteWebDriver = device.getRemoteWebDriver();

    if (remoteWebDriver == null) {
      log.info("no appium session exists to quit....returning...");
      return;
    }
    remoteWebDriver.quit();
    device.setRemoteWebDriver(null);
  }
}
