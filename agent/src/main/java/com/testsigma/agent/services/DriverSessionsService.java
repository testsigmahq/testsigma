/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.services;

import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.config.AgentConfig;
import com.testsigma.agent.constants.MobileOs;
import com.testsigma.agent.dto.WebDriverSettingsDTO;
import com.testsigma.agent.exception.TestsigmaException;
import com.testsigma.agent.http.ServerURLBuilder;
import com.testsigma.agent.http.WebAppHttpClient;
import com.testsigma.agent.mobile.*;
import com.testsigma.agent.mobile.ios.IosDeviceService;
import com.testsigma.agent.request.DriverSessionRequest;
import com.testsigma.agent.request.MobileInspectionRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.TestsigmaDriver;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.drivers.mobile.AndroidDriver;
import com.testsigma.automator.drivers.mobile.IosDriver;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.http.HttpResponse;
import com.testsigma.automator.mobile.ios.AppInstaller;
import com.testsigma.automator.utilities.PathUtil;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

@Data
@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DriverSessionsService {
  private final SessionContainer sessionContainer;
  private final DeviceContainer deviceContainer;
  private final WebAppHttpClient httpClient;
  private final MobileAutomationServer mobileAutomationServer;
  private final AgentConfig agentConfig;
  private final IosDeviceService iosDeviceService;
  private final MobileAutomationServerService mobileAutomationServerService;

  public String createSession(DriverSessionRequest driverSessionRequest) throws Exception {
    com.testsigma.agent.dto.WebDriverSettingsDTO webDriverSettingsDTO;
    if (ExecutionLabType.Hybrid.equals(driverSessionRequest.getExecutionLabType())
      && WorkspaceType.isMobileApp(driverSessionRequest.getWorkspaceType())) {
      disconnectDeviceSession(driverSessionRequest.getUniqueId());
      driverSessionRequest.setWebDriverServerUrl(new URL(mobileAutomationServer.getServerURL()));
    }
    webDriverSettingsDTO = fetchWebDriverSettings(driverSessionRequest);
    log.info("Creating a remote web driver session with settings - " + webDriverSettingsDTO);
    TestsigmaDriver testsigmaDriver = getDriverInstance(driverSessionRequest);
    List<WebDriverCapability> caps = webDriverSettingsDTO.getWebDriverCapabilities();
    addMissingTimeOutCapability(caps);
    handleLocalDevice(caps, driverSessionRequest);
    setRemoteServerURL(testsigmaDriver, driverSessionRequest, webDriverSettingsDTO);
    testsigmaDriver.setCapabilities(caps);
    RemoteWebDriver remoteWebDriver = testsigmaDriver.createSession();
    String sessionId = populateSessionIdMaps(remoteWebDriver, driverSessionRequest);
    sendMobileSessionStartedRequest(sessionId, driverSessionRequest);
    return sessionId;
  }

  private com.testsigma.agent.dto.WebDriverSettingsDTO fetchWebDriverSettings(DriverSessionRequest driverSessionRequest)
    throws IOException, TestsigmaException {
    HttpResponse<com.testsigma.agent.dto.WebDriverSettingsDTO> response;
    String authHeader = null;
    if (agentConfig.getJwtApiKey() == null) {
      authHeader = WebAppHttpClient.BEARER + " " + driverSessionRequest.getJwtApiKey();
    } else {
      authHeader = WebAppHttpClient.BEARER + " " + agentConfig.getJwtApiKey();
    }
    response = httpClient.post(ServerURLBuilder.webDriverSettingsURL(), driverSessionRequest, new TypeReference<>() {
    }, authHeader);
    if (response.getStatusCode() != HttpStatus.OK.value()) {
      throw new TestsigmaException("Could not fetch web driver settings from server "
        + response.getStatusCode() + " - " + response.getStatusMessage());
    }
    return response.getResponseEntity();
  }

  private String populateSessionIdMaps(RemoteWebDriver remoteWebDriver, DriverSessionRequest driverSessionRequest)
    throws Exception {
    String sessionId = remoteWebDriver.getSessionId().toString();

    if (sessionContainer.getDeviceToSessionMap().containsKey(driverSessionRequest.getUniqueId())) {
      deleteSession(sessionContainer.getDeviceToSessionMap().get(driverSessionRequest.getUniqueId()));
    }
    sessionContainer.getSessionMap().put(sessionId, remoteWebDriver);
    sessionContainer.getSessionToDeviceIdMap().put(sessionId, driverSessionRequest.getUniqueId());
    sessionContainer.getDeviceToSessionMap().put(driverSessionRequest.getUniqueId(), sessionId);
    return sessionId;
  }

  private void addMissingTimeOutCapability(List<WebDriverCapability> caps) {
    WebDriverCapability newCommandTimeoutCapability = caps.stream().filter(cap -> cap.getCapabilityName()
      .equals(TSCapabilityType.NEW_COMMAND_TIMEOUT)).findFirst().orElse(null);
    if (newCommandTimeoutCapability == null) {
      caps.add(new WebDriverCapability(TSCapabilityType.NEW_COMMAND_TIMEOUT, 0));
    }
  }

  private void setRemoteServerURL(TestsigmaDriver testsigmaDriver, DriverSessionRequest driverSessionRequest,
                                  WebDriverSettingsDTO webDriverSettingsDTO) throws MalformedURLException {
    if (driverSessionRequest.getExecutionLabType().equals(ExecutionLabType.Hybrid)) {
      testsigmaDriver.setRemoteServerURL(new URL(mobileAutomationServerService.getMobileAutomationServer().getServerURL()));
    } else {
      testsigmaDriver.setRemoteServerURL(webDriverSettingsDTO.getWebDriverServerUrl());
    }
  }

  private void handleLocalDevice(List<WebDriverCapability> caps, DriverSessionRequest driverSessionRequest)
          throws TestsigmaException, AutomatorException {
    if (driverSessionRequest.getExecutionLabType().equals(ExecutionLabType.Hybrid)) {
      if (driverSessionRequest.getWorkspaceType() == WorkspaceType.IOSNative) {
        setupIosDevice(caps, driverSessionRequest);
      }
    }
  }
  public void setupIosDevice(List<WebDriverCapability> caps, DriverSessionRequest driverSessionRequest)
          throws TestsigmaException, AutomatorException {
    MobileDevice device = deviceContainer.getDevice(driverSessionRequest.getUniqueId());
    iosDeviceService.setupWda(device);
    WebDriverCapability bundleIdCapability = caps.stream().filter(cap -> cap.getCapabilityName()
            .equals(TSCapabilityType.BUNDLE_ID)).findFirst().orElse(null);
    if ((bundleIdCapability == null) || StringUtils.isBlank((String) bundleIdCapability.getCapabilityValue())) {
      WebDriverCapability appCapability = caps.stream().filter(cap -> cap.getCapabilityName()
              .equals(MobileCapabilityType.APP)).findFirst().orElse(null);
      AppPathType appPathType = driverSessionRequest.getApplicationPathType();
      if ((appCapability != null) && appPathType != AppPathType.APP_DETAILS) {
        caps.remove(appCapability);
        String appPresignedUrl = (String) appCapability.getCapabilityValue();
        String bundleId = iosDeviceService.installApp(device, appPresignedUrl, device.getIsEmulator());
        caps.add(new WebDriverCapability(TSCapabilityType.BUNDLE_ID, bundleId));
      }
    }
  }

  private TestsigmaDriver getDriverInstance(DriverSessionRequest driverSessionRequest) {
    TestsigmaDriver testsigmaDriver = new TestsigmaDriver();
    if (Platform.Android.equals(driverSessionRequest.getPlatform())) {
      testsigmaDriver = new AndroidDriver();
    } else if (Platform.iOS.equals(driverSessionRequest.getPlatform())) {
      testsigmaDriver = new IosDriver();
    }
    return testsigmaDriver;
  }

  public void deleteSession(String sessionId) throws Exception {
    log.debug("Removing session from appium server");
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    new TestsigmaDriver().deleteSession(remoteWebDriver);
    if (sessionContainer.getSessionMap().containsKey(sessionId)) {
      sessionContainer.getSessionMap().remove(sessionId);
      String deviceId = sessionContainer.getSessionToDeviceIdMap().get(sessionId);
      sessionContainer.getSessionToDeviceIdMap().remove(sessionId);
      sessionContainer.getDeviceToSessionMap().remove(deviceId);
      if (deviceContainer != null && deviceContainer.getDeviceMap().containsKey(deviceId)) {
        MobileDevice device = deviceContainer.getDevice(deviceId);
        if((device != null) && (device.getOsName() == MobileOs.IOS)) {
          iosDeviceService.cleanupWda(device);
        } else {
          log.info("Device os is not iOS. Skipping WDA cleanup");
        }
      }
    } else {
      log.info("Session ID - " + sessionId + " doesn't exist.");
    }
  }

  public String getSession(String sessionId) throws Exception {
    RemoteWebDriver remoteWebDriver = sessionContainer.getSessionMap().get(sessionId);
    Response response = remoteWebDriver.getCommandExecutor().execute(new Command(new SessionId(sessionId), "status"));
    return response.getState() + "-" + response.getStatus();
  }

  public void disconnectDeviceSession(String uniqueId) throws Exception {
    String sessionId = sessionContainer.getDeviceToSessionMap().get(uniqueId);
    if (sessionId != null) {
      log.debug("Detected an existing inspection session for device - " + uniqueId + " , Stopping the session.");
      deleteSession(sessionId);
    }
  }

  private void sendMobileSessionStartedRequest(String sessionId, DriverSessionRequest driverSessionRequest)
    throws IOException {
    MobileInspectionRequest mobileInspectionRequest = new MobileInspectionRequest();
    mobileInspectionRequest.setId(driverSessionRequest.getMobileSessionId());
    mobileInspectionRequest.setStatus(MobileInspectionStatus.STARTED);
    mobileInspectionRequest.setStartedAt(new Timestamp(System.currentTimeMillis()));
    mobileInspectionRequest.setLastActiveAt(new Timestamp(System.currentTimeMillis()));
    mobileInspectionRequest.setSessionId(sessionId);
    String authHeader = null;
    String Uuid = null;
    if (agentConfig.getJwtApiKey() == null) {
      authHeader = WebAppHttpClient.BEARER + " " + driverSessionRequest.getJwtApiKey();
      Uuid = driverSessionRequest.getAgentUUID();
    } else {
      authHeader = WebAppHttpClient.BEARER + " " + agentConfig.getJwtApiKey();
      Uuid = agentConfig.getUUID();
    }

    HttpResponse<String> mobileInspectionResponse = httpClient.put(ServerURLBuilder.mobileSessionURL(Uuid,
      driverSessionRequest.getMobileSessionId()), mobileInspectionRequest, new TypeReference<>() {
    }, authHeader);
    log.debug(mobileInspectionResponse.getStatusCode() + " - " + mobileInspectionResponse.getResponseText());
  }

  public File driverExecutableExists(String browserNameKey, String browserMajorVersion) throws TestsigmaException {
    try {
      String driversFolderPath = PathUtil.getInstance().getDriversPath();
      String driverPath = AutomatorConfig.getInstance().getAppBridge().getDriverExecutablePath(browserNameKey,
        browserMajorVersion);
      File driverFile = Paths.get(driversFolderPath, driverPath).toFile();
      log.info("Checking if driver executable exists at : " + driverFile.getAbsolutePath());
      return driverFile.exists() ? driverFile : null;
    } catch (AutomatorException e) {
      log.error(e.getMessage(), e);
      throw new TestsigmaException(e.getMessage(), e);
    }
  }
}
