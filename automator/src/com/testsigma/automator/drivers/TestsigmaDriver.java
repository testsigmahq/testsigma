/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.drivers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.runners.EnvironmentRunner;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
@Data
public class TestsigmaDriver {
  protected TestDeviceEntity testDeviceEntity;
  protected RemoteWebDriver remoteWebDriver;
  protected TestDeviceSettings settings;
  protected ExecutionLabType executionLabType;
  protected List<WebDriverCapability> capabilities;
  protected URL remoteServerURL;
  protected String executionName;
  protected WebDriverSettingsDTO webDriverSettings;


  public TestsigmaDriver() {
    this.testDeviceEntity = EnvironmentRunner.getRunnerEnvironmentEntity();
    //Condition For Handling Mobile Inspections
    if (this.testDeviceEntity != null) {
      this.settings = testDeviceEntity.getEnvSettings();
      this.executionLabType = testDeviceEntity.getExecutionLabType();
      this.capabilities = new ArrayList<>();
      this.executionName = getExecutionName();
    }
  }

  protected void setCapabilities() throws AutomatorException, MalformedURLException {
  }

  protected void setCommonCapabilities() throws AutomatorException {
  }


  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
  }

  protected void setTestsigmaLabCapabilities() throws AutomatorException {
    setTestsigmaLabRemoteServerUrl();
  }

  protected void setTimeouts() throws AutomatorException {
    if (settings != null && settings.getElementTimeout() != null) {
      remoteWebDriver.manage().timeouts().implicitlyWait(
        Duration.ofSeconds(settings.getElementTimeout()));
    }
    if (settings != null && settings.getPageLoadTimeout() != null) {
      remoteWebDriver.manage().timeouts().implicitlyWait(
              Duration.ofSeconds(settings.getPageLoadTimeout()));
    }

  }

  public RemoteWebDriver createSession() throws AutomatorException, MalformedURLException {
    DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
    if (capabilities.size() == 0) {
      setDriverCapabilities();
      setCapabilities();
    }
    for (WebDriverCapability webDriverCapability : capabilities) {
      desiredCapabilities.setCapability(webDriverCapability.getCapabilityName(), webDriverCapability.getCapabilityValue());
    }

    log.info("Creating a driver to URL:  " + remoteServerURL);
    log.info("Creating a driver with capabilities - " + capabilities);
    return createDriver(desiredCapabilities);
  }

  protected RemoteWebDriver createDriver(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    return new RemoteWebDriver(webDriverSettings.getWebDriverServerUrl(), desiredCapabilities);
  }

  public void deleteSession(RemoteWebDriver remoteWebDriver) throws AutomatorException {
    if (remoteWebDriver == null) {
      log.info("no session exists to quit....returning...");
      return;
    }
    try {
      try {
        remoteWebDriver.quit();
      } catch (Exception e) {
        remoteWebDriver.quit();
        log.error(e.getMessage(), e);
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw e;
    }
  }

  protected void setAdditionalCapabilities(List<WebDriverCapability> additionalCapabilitiesList)
    throws AutomatorException {
    if (additionalCapabilitiesList != null) {
      for (WebDriverCapability capability : additionalCapabilitiesList) {
        String name = capability.getCapabilityName();
        if (!name.equals("")) {
          if (name.equals("appium:chromeOptions")) {
            capabilities.add(new WebDriverCapability(name, ImmutableMap.of("w3c", false)));
          } else {
            capabilities.add(new WebDriverCapability(name, capability.getCapabilityValue()));
          }
        }

      }
    }
  }

  protected void setDriverCapabilities() throws AutomatorException {
    webDriverSettings = AutomatorConfig.getInstance().getAppBridge().getWebDriverSettings(
      this.getTestDeviceEntity().getEnvironmentResultId());
    setUserNameAndAccessKey();
  }

  protected String getUserName() {
    for (WebDriverCapability capability : webDriverSettings.getWebDriverCapabilities()) {
      if (capability.getCapabilityName().equals("username") || capability.getCapabilityName().equals("user")) {
        return capability.getCapabilityValue().toString();
      }
    }
    return null;
  }

  protected String getPlatform() {
    for (WebDriverCapability capability : webDriverSettings.getWebDriverCapabilities()) {
      if (capability.getCapabilityName().equals("platformName") || capability.getCapabilityName().equals("platform") || capability.getCapabilityName().equals("os_version")) {
        return capability.getCapabilityValue().toString();
      }
    }
    return null;
  }


  protected String getAccessKey() {
    for (WebDriverCapability capability : webDriverSettings.getWebDriverCapabilities()) {
      if (capability.getCapabilityName().equals("accessKey")) {
        return capability.getCapabilityValue().toString();
      }
    }
    return null;
  }

  protected void setUserNameAndAccessKey() {
    this.testDeviceEntity.setUsername(getUserName());
    this.testDeviceEntity.setAccessKey(getAccessKey());
  }

  protected Map<String, Object> parseCapabilities(String cap, String capabilityType) throws AutomatorException {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> capabilities;
    try {
      capabilities = mapper.readValue(cap, Map.class);
    } catch (IOException e) {
      throw new AutomatorException("Invalid " + capabilityType
        + " options provided in the desired capabilities configuration.");
    }
    return capabilities;
  }

  protected void setPlatformSpecificCapabilities() throws AutomatorException, MalformedURLException {
    switch (executionLabType) {
      case TestsigmaLab:
        setTestsigmaLabCapabilities();
        break;
      case Hybrid:
        setHybridCapabilities();
        break;
      case PrivateGrid:
        setPrivateGridCapabilities();
        break;
      default:
        log.error("Unsupported execution lab type - " + executionLabType);
    }
  }

  private String getExecutionName() {
    String name = "[Trial] - Mobile Inspection";
    if (settings != null) {
      String runBy = ObjectUtils.defaultIfNull(settings.getRunBy(), "");
      String executionRunId = settings.getExecutionRunId().toString();
      String executionName = settings.getExecutionName();
      name = String.format("[%s] - %s - %s", executionRunId, runBy, executionName);
      name = name.replaceAll("[^a-zA-Z1-90_\\s\\[\\]\\:\\-@\\.]*", "");
    }
    return name;
  }

  protected void setTestsigmaLabRemoteServerUrl() throws AutomatorException {
    setRemoteServerURL(webDriverSettings.getWebDriverServerUrl());
  }

  protected void setPrivateGridCapabilities(){
    setRemoteServerURL(webDriverSettings.getWebDriverServerUrl());
  }

  protected void setHybridRemoteServerUrl(String url) throws MalformedURLException {
    setRemoteServerURL(new URL(url));
  }

}
