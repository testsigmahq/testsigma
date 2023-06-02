/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.mobile;

import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.dto.AgentDeviceDTO;
import com.testsigma.agent.exception.DeviceContainerException;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mappers.MobileDeviceMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.entity.Browsers;
import com.testsigma.automator.entity.OsBrowserType;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DeviceContainer {

  // device map table (key: device uniqueId, value: MobileDevice class)
  @Getter
  private final Map<String, MobileDevice> deviceMap = new ConcurrentHashMap<>();
  @Getter
  private final Map<String, MobileDevice> muxDeviceMap = new ConcurrentHashMap<>();
  private final WebAppHttpClient httpClient;
  private final MobileDeviceMapper mobileDeviceMapper;
  private final AgentConfig agentConfig;
  private final MobileAutomationServerService mobileAutomationServerService;

  public void addDevice(MobileDevice mobileDevice) throws DeviceContainerException {
    try {
      if (mobileDevice == null) {
        return;
      }
      String deviceUniqueId = mobileDevice.getUniqueId();
      if (deviceUniqueId != null && !deviceMap.containsKey(deviceUniqueId)) {
        AgentDeviceDTO agentDeviceDTO = getAgentDevice(deviceUniqueId);
        if (agentDeviceDTO == null) {
          log.info("Found a new device. Adding an entry for the device: " + mobileDevice);
          createAgentDevice(mobileDeviceMapper.map(mobileDevice));
        } else {
          log.info("Found an existing device. Updating the entry for the device: " + mobileDevice);
          mobileDeviceMapper.merge(mobileDevice, agentDeviceDTO);
          updateAgentDevice(agentDeviceDTO);
        }
        deviceMap.put(mobileDevice.getUniqueId(), mobileDevice);
        if (mobileDevice.getMuxDeviceId() != null) {
          muxDeviceMap.put(mobileDevice.getMuxDeviceId(), mobileDevice);
        }
        mobileAutomationServerService.installDrivers(mobileDevice.getOsName(), mobileDevice.getUniqueId());
        syncBrowserDrivers(mobileDevice);

      } else {
        log.info("Device " + deviceUniqueId + " already in container...");
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DeviceContainerException(e.getMessage(), e);
    }
  }

  public void syncBrowserDrivers(MobileDevice mobileDevice) {
    log.info("Syncing Browser Drivers For Mobile Devices - " + mobileDevice);
    List<AgentBrowser> browserList = mobileDevice.getBrowserList();
    if (browserList == null) {
      return;
    }
    for (AgentBrowser browserObj : browserList) {
      try {
        log.info("Trying to sync driver for mobile browser - " + browserObj);
        OsBrowserType browserType = browserObj.getName();
        String browserVersion = browserObj.getMajorVersion() + "";
        Browsers browser = OsBrowserType.getBrowserType(browserType);
        String driverPath = AutomatorConfig.getInstance().getAppBridge().getDriverExecutablePath(browser.getKey(),
          browserVersion);
      } catch (AutomatorException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public void deleteDevice(String uniqueId) throws DeviceContainerException {
    try {
      for (Entry<String, MobileDevice> entry : deviceMap.entrySet()) {
        String key = entry.getKey();
        MobileDevice device = entry.getValue();
        if (key.equals(uniqueId)) {
          log.info("Removing the device " + key + " from device container");
          AgentDeviceDTO agentDeviceDTO = getAgentDevice(key);
          if (agentDeviceDTO != null) {
            agentDeviceDTO.setIsOnline(false);
            updateAgentDevice(agentDeviceDTO);
          }
          deviceMap.remove(key);
          muxDeviceMap.remove(device.getMuxDeviceId());
          break;
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DeviceContainerException(e.getMessage(), e);
    }
  }

  public void disconnectDevices() throws DeviceContainerException {
    try {
      for (Entry<String, MobileDevice> entry : deviceMap.entrySet()) {
        String key = entry.getKey();
        MobileDevice device = entry.getValue();
        log.info("Removing the device " + key + " from device container");
        AgentDeviceDTO agentDeviceDTO = getAgentDevice(key);
        if (agentDeviceDTO != null) {
          agentDeviceDTO.setIsOnline(false);
          updateAgentDevice(agentDeviceDTO);
        }
        deviceMap.remove(key);
        muxDeviceMap.remove(device.getMuxDeviceId());
      }
      log.info("Removed all the devices - " + deviceMap.size());
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DeviceContainerException(e.getMessage(), e);
    }
  }

  public MobileDevice getDevice(String uniqueId) throws TestsigmaException {
    if ((uniqueId == null) || !this.deviceMap.containsKey(uniqueId)) {
      throw new TestsigmaException(String.format("There is no device with unique Id %s in device container", uniqueId));
    }
    return this.deviceMap.get(uniqueId);
  }

  public MobileDevice getDeviceByMuxId(String muxId) throws TestsigmaException {
    if ((muxId == null) || !this.muxDeviceMap.containsKey(muxId)) {
      throw new TestsigmaException(String.format("There is no device with mux Id %s in device container", muxId));
    }
    return this.muxDeviceMap.get(muxId);
  }

  private AgentDeviceDTO getAgentDevice(String uniqueId) throws DeviceContainerException {
    try {
      AgentDeviceDTO agentDeviceDTO = null;
      String agentUuid = agentConfig.getUUID();
      String authHeader = WebAppHttpClient.BEARER + " " + this.agentConfig.getJwtApiKey();
      HttpResponse<AgentDeviceDTO> response =
        httpClient
          .get(ServerURLBuilder.agentConnectedDeviceURL(agentUuid, uniqueId), new TypeReference<>() {
          }, authHeader);
      if (response.getStatusCode() == HttpStatus.OK.value()) {
        agentDeviceDTO = response.getResponseEntity();
      }
      return agentDeviceDTO;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DeviceContainerException(e.getMessage(), e);
    }
  }

  private AgentDeviceDTO createAgentDevice(AgentDeviceDTO agentDeviceRequest) throws DeviceContainerException {
    try {
      AgentDeviceDTO agentDeviceDTO = null;
      String agentUuid = agentConfig.getUUID();
      String authHeader = WebAppHttpClient.BEARER + " " + this.agentConfig.getJwtApiKey();
      HttpResponse<AgentDeviceDTO> response =
        httpClient.post(ServerURLBuilder.agentConnectedDevicesURL(agentUuid), agentDeviceRequest,
          new TypeReference<>() {
          }, authHeader);
      if (response.getStatusCode() == HttpStatus.CREATED.value()) {
        agentDeviceDTO = response.getResponseEntity();
      }
      return agentDeviceDTO;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DeviceContainerException(e.getMessage(), e);
    }
  }

  private AgentDeviceDTO updateAgentDevice(AgentDeviceDTO agentDeviceRequest) throws DeviceContainerException {
    try {
      AgentDeviceDTO agentDeviceDTO = null;
      String agentUuid = agentConfig.getUUID();
      String authHeader = WebAppHttpClient.BEARER + " " + this.agentConfig.getJwtApiKey();
      HttpResponse<AgentDeviceDTO> response =
        httpClient.put(ServerURLBuilder.agentConnectedDeviceURL(agentUuid, agentDeviceRequest.getUniqueId()),
          agentDeviceRequest,
          new TypeReference<>() {
          }, authHeader);
      if (response.getStatusCode() == HttpStatus.OK.value()) {
        agentDeviceDTO = response.getResponseEntity();
      }
      return agentDeviceDTO;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new DeviceContainerException(e.getMessage(), e);
    }
  }
}

