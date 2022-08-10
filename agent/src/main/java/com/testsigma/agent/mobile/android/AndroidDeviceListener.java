/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */
package com.testsigma.agent.mobile.android;

import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.constants.MobileOs;
import com.testsigma.agent.exception.AdbCommandExecutionException;
import com.testsigma.agent.exception.NativeBridgeException;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mappers.MobileDeviceMapper;
import com.testsigma.agent.mobile.DeviceContainer;
import com.testsigma.agent.mobile.DeviceListener;
import com.testsigma.agent.mobile.MobileDevice;
import com.testsigma.agent.mobile.SessionContainer;
import com.testsigma.agent.mobile.ios.DeveloperImageService;
import com.testsigma.agent.mobile.ios.IosDeviceService;
import com.testsigma.agent.services.DriverSessionsService;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.testsigma.automator.entity.OsBrowserType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
public class AndroidDeviceListener extends DeviceListener implements AndroidDebugBridge.IDeviceChangeListener {

  private AndroidDebugBridge adBridge;

  public AndroidDeviceListener(
    MobileDeviceMapper mobileDeviceMapper,
    WebAppHttpClient httpClient,
    DeviceContainer deviceContainer,
    AgentConfig agentConfig,
    AdbBridge adbBridge,
    CommandExecutor commandExecutor,
    SessionContainer sessionContainer,
    DriverSessionsService driverSessionsService,
    IosDeviceService iosDeviceService,
    DeveloperImageService developerImageService
  ) {
    super(mobileDeviceMapper, httpClient, deviceContainer, agentConfig,
      adbBridge, commandExecutor, sessionContainer, driverSessionsService, iosDeviceService, developerImageService);
    this.listenerType = "Android";
  }

  public void initializeNativeBridge() throws NativeBridgeException {
    try {
      this.adBridge = adbBridge.getADBInstance();
      if (!adBridge.hasInitialDeviceList()) {
        waitForAdbInitialization();
      }
      bridgeInitialized = true;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new NativeBridgeException(e.getMessage(), e);
    }
  }

  public void getInitialDeviceList() {
    try {
      if (agentConfig.getRegistered().equals(Boolean.FALSE)) {
        log.debug("Skipping initial agent devices collection since agent is not registered...");
        return;
      }
      log.debug("Started getting initial agent devices connected...");
      IDevice[] devices = adBridge.getDevices();

      for (IDevice device : devices) {
        if (IDevice.DeviceState.ONLINE.equals(device.getState())) {
          MobileDevice mobileDevice = mobileDeviceMapper.map(device);
          mobileDevice.setIDevice(device);
          populateOtherAttributes(mobileDevice, device);
          this.addDevice(mobileDevice);
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void addDeviceListenerCallback() throws TestsigmaException {
    try {
      if (agentConfig.getRegistered().equals(Boolean.FALSE)) {
        log.debug("Skipping agent devices listener callback registration since agent is not registered...");
        return;
      }
      log.debug("Registering agent device listener callbacks...");
      AndroidDebugBridge.addDeviceChangeListener(this);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void removeDeviceListenerCallback() throws TestsigmaException {
    try {
      if (agentConfig.getRegistered().equals(Boolean.FALSE)) {
        log.debug("Skipping agent devices listener callback de-registration since agent is not registered...");
        return;
      }
      log.debug("De-Registering agent device listener callbacks...");
      AndroidDebugBridge.removeDeviceChangeListener(this);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  @Override
  public void deviceConnected(IDevice device) {
    log.info("IosDevice connected event received by Listener");
    try {
      if (IDevice.DeviceState.ONLINE.equals(device.getState())) {
        MobileDevice mobileDevice = mobileDeviceMapper.map(device);
        mobileDevice.setIDevice(device);
        populateOtherAttributes(mobileDevice, device);
        this.addDevice(mobileDevice);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public void deviceDisconnected(IDevice device) {
    log.info("IosDevice disconnected event received by Listener");
    try {
      MobileDevice mobileDevice = mobileDeviceMapper.map(device);
      this.removeDevice(mobileDevice);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @Override
  public void deviceChanged(IDevice device, int i) {
    log.info("IosDevice changed event received by Listener");
    try {
      MobileDevice mobileDevice = mobileDeviceMapper.map(device);
      if (IDevice.DeviceState.ONLINE.equals(device.getState())) {
        populateOtherAttributes(mobileDevice, device);
      }
      mobileDevice.setIDevice(device);
      this.updateDevice(mobileDevice);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void populateOtherAttributes(MobileDevice mobileDevice, IDevice device) throws AdbCommandExecutionException {
    mobileDevice.setScreenWidth(commandExecutor.getScreenWidth(device));
    mobileDevice.setScreenHeight(commandExecutor.getScreenHeight(device));
    populateBrowserList(mobileDevice, device);
    mobileDevice.setOsName(MobileOs.ANDROID);
  }

  private void populateBrowserList(MobileDevice mobileDevice, IDevice device) throws AdbCommandExecutionException {
    boolean isChromeInstalled = commandExecutor.isPackageInstalled(device, "com.android.chrome");
    if (isChromeInstalled) {
      List<AgentBrowser> browserList = new ArrayList<>();
      String version = commandExecutor.getChromeVersion(device);
      AgentBrowser browser = new AgentBrowser(OsBrowserType.Chrome, version, 64);
      browserList.add(browser);
      mobileDevice.setBrowserList(browserList);
    }
  }

  private void waitForAdbInitialization() {
    byte retries = 0;
    while (!adBridge.hasInitialDeviceList()) {
      log.debug("Waiting for initial list of device post ADB initialization.....");
      try {
        Thread.sleep(100L);
        retries++;
      } catch (InterruptedException interruptedException) {
        log.warn("Interrupted while waiting for initial device list from ADB");
      }

      if (retries > 40) {
        log.warn("Timed out while waiting for initial device list from ADB");
      }
    }
  }
}
