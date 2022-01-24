/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile;

import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.exception.DeviceContainerException;
import com.testsigma.agent.exception.NativeBridgeException;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mappers.MobileDeviceMapper;
import com.testsigma.agent.mobile.android.AdbBridge;
import com.testsigma.agent.mobile.android.CommandExecutor;
import com.testsigma.agent.mobile.ios.DeveloperImageService;
import com.testsigma.agent.mobile.ios.IosDeviceService;
import com.testsigma.agent.services.DriverSessionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class DeviceListener implements Runnable {
  protected final MobileDeviceMapper mobileDeviceMapper;
  protected final WebAppHttpClient httpClient;
  protected final DeviceContainer deviceContainer;
  protected final AgentConfig agentConfig;
  protected final AdbBridge adbBridge;
  protected final CommandExecutor commandExecutor;
  protected final SessionContainer sessionContainer;
  protected final DriverSessionsService driverSessionsService;
  protected final IosDeviceService iosDeviceService;
  protected final DeveloperImageService developerImageService;

  protected Boolean bridgeInitialized = false;
  protected String listenerType;

  public void run() {
    log.debug("Device listener triggered for " + listenerType + " devices");
    if (!shouldListen()) {
      return;
    }

    try {
      initializeNativeBridge();
      getInitialDeviceList();
      addDeviceListenerCallback();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void addDevice(MobileDevice device) throws DeviceContainerException {
    if (!bridgeInitialized) {
      log.info("Native bridge is not yet initialized");
      return;
    }
    if (!device.getIsOnline()) {
      log.info("Device is offline. Skipping the device from container.");
      return;
    }
    deviceContainer.addDevice(device);
  }

  public void removeDevice(MobileDevice device) throws DeviceContainerException {
    try {
      driverSessionsService.disconnectDeviceSession(device.getUniqueId());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    deviceContainer.deleteDevice(device.getUniqueId());
  }

  public void updateDevice(MobileDevice device) throws DeviceContainerException {
    this.addDevice(device);
  }

  public abstract void getInitialDeviceList() throws TestsigmaException, DeviceContainerException;

  public abstract void initializeNativeBridge() throws TestsigmaException, NativeBridgeException;

  public abstract void addDeviceListenerCallback() throws TestsigmaException;

  public boolean shouldListen() {
    boolean listen = true;
    if (agentConfig.getRegistered().equals(Boolean.FALSE)) {
      log.debug("Agent is not yet registered...skipping device listener...");
      listen = false;
    }

    return listen;
  }

  public void syncInitialDeviceStatus() {
    try {
      if (shouldListen()) {
        String agentUuid = agentConfig.getUUID();
        String authHeader = WebAppHttpClient.BEARER + " " + this.agentConfig.getJwtApiKey();
        httpClient.put(ServerURLBuilder.agentDeviceStatusURL(agentUuid), "", null, authHeader);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }
}
