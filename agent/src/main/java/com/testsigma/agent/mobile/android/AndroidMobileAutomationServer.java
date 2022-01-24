/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.mobile.android;

import com.testsigma.agent.exception.MobileLibraryInstallException;
import com.testsigma.agent.mobile.DeviceContainer;
import com.testsigma.agent.mobile.MobileDevice;
import com.testsigma.agent.utils.PathUtil;
import com.android.ddmlib.IDevice;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class AndroidMobileAutomationServer {
  private final DeviceContainer deviceContainer;
  private final CommandExecutor commandExecutor;

  private final String UI_AUTOMATOR2_SERVER_TEST_APK = "appium-uiautomator2-server-test.apk";
  private final String UI_AUTOMATOR2_SERVER_APK = "appium-uiautomator2-server.apk";
  private final String APPIUM_SETTINGS_APK = "settings_apk-debug.apk";
  private final String UI_AUTOMATOR2_PACKAGE = "io.appium.uiautomator2.server";
  private final String UI_AUTOMATOR2_TEST_PACKAGE = "io.appium.uiautomator2.server.test";
  private final String APPIUM_SETTINGS_PACKAGE = "io.appium.settings";

  public void installDrivers(String uniqueId) throws MobileLibraryInstallException {
    try {
      MobileDevice mobileDevice = deviceContainer.getDevice(uniqueId);
      IDevice device = mobileDevice.getIDevice();

      this.installAppiumSettings(device);
      this.startAppiumSettings(device);
      this.installUIAutomatorServer(device);
      this.installUIAutomatorServerTest(device);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  public void uninstallDrivers(String uniqueId)
    throws MobileLibraryInstallException {
    try {
      MobileDevice mobileDevice = deviceContainer.getDevice(uniqueId);
      IDevice device = mobileDevice.getIDevice();

      this.uninstallUIAutomatorServer(device);
      this.uninstallUIAutomatorServerTest(device);
      this.uninstallAppiumSettings(device);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private File uiAutomatorServerTestAPK() {
    return new File(PathUtil.getInstance().getMobileAutomationServerPath(),
      "apks" + File.separator + UI_AUTOMATOR2_SERVER_TEST_APK);
  }

  private File uiAutomatorServerAPK() {
    return new File(PathUtil.getInstance().getMobileAutomationServerPath(), "apks" + File.separator + UI_AUTOMATOR2_SERVER_APK);
  }

  private File appiumSettingAPK() {
    return new File(PathUtil.getInstance().getMobileAutomationServerPath(), "apks" + File.separator + APPIUM_SETTINGS_APK);
  }

  private void installUIAutomatorServer(IDevice device) throws MobileLibraryInstallException {
    try {
//      if (commandExecutor.isPackageInstalled(device, UI_AUTOMATOR2_PACKAGE)) {
//        log.info("io.appium.uiautomator2.server is already installed. So Skipping the install");
//        return;
//      }
      log.info("Installing UIAutomatorServer on device" + device.getSerialNumber());
      device.installPackage(uiAutomatorServerAPK().getAbsolutePath(), true);
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private void installUIAutomatorServerTest(IDevice device) throws MobileLibraryInstallException {
    try {
//      if (commandExecutor.isPackageInstalled(device, UI_AUTOMATOR2_TEST_PACKAGE)) {
//        log.info("io.appium.uiautomator2.server.test is already installed. So Skipping the install");
//        return;
//      }
      log.info("Installing UIAutomatorServerTest on device" + device.getSerialNumber());
      device.installPackage(uiAutomatorServerTestAPK().getAbsolutePath(), true);
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private void installAppiumSettings(IDevice device) throws MobileLibraryInstallException {
    try {
//      if (commandExecutor.isPackageInstalled(device, APPIUM_SETTINGS_PACKAGE)) {
//        log.info("io.appium.settings is already installed. So Skipping the install");
//        return;
//      }
      log.info("Installing AppiumSettings on device" + device.getSerialNumber());
      device.installPackage(appiumSettingAPK().getAbsolutePath(), true);
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private void startAppiumSettings(IDevice device) throws MobileLibraryInstallException {
    try {
      commandExecutor.executeCommand(device, "am handleStartEvent -n " + APPIUM_SETTINGS_PACKAGE
        + "/" + APPIUM_SETTINGS_PACKAGE + ".Settings");
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private void uninstallUIAutomatorServer(IDevice device) throws MobileLibraryInstallException {
    try {
      if (!commandExecutor.isPackageInstalled(device, UI_AUTOMATOR2_PACKAGE)) {
        log.info("io.appium.uiautomator2.server is not installed. So Skipping the uninstall");
      }
      log.info("Uninstalling UIAutomatorServer on device" + device.getSerialNumber());
      device.uninstallPackage(UI_AUTOMATOR2_PACKAGE);
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private void uninstallUIAutomatorServerTest(IDevice device) throws MobileLibraryInstallException {
    try {
      if (!commandExecutor.isPackageInstalled(device, UI_AUTOMATOR2_TEST_PACKAGE)) {
        log.info("io.appium.uiautomator2.server is not installed. So Skipping the uninstall");
      }
      log.info("Uninstalling UIAutomatorServerTest on device" + device.getSerialNumber());
      device.uninstallPackage(UI_AUTOMATOR2_TEST_PACKAGE);
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }

  private void uninstallAppiumSettings(IDevice device) throws MobileLibraryInstallException {
    try {
      if (!commandExecutor.isPackageInstalled(device, APPIUM_SETTINGS_PACKAGE)) {
        log.info("io.appium.settings is not installed. So Skipping the uninstall");
      }
      log.info("Uninstalling AppiumSettings on device" + device.getSerialNumber());
      device.uninstallPackage(APPIUM_SETTINGS_PACKAGE);
    } catch (Exception e) {
      throw new MobileLibraryInstallException(e.getMessage(), e);
    }
  }
}
