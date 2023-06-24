package com.testsigma.service;


import com.testsigma.constants.TSCapabilityType;
import com.testsigma.dto.WebDriverSettingsDTO;
import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class HybridDriverSettingsService extends DriverSettingsService {
  public static final String LOCAL_HOST_URL = "http://127.0.0.1:8080";
  @Autowired
  private AgentDeviceService agentDeviceService;

  @Override
  public WebDriverSettingsDTO driverSettings(TestDevice testDevice, WorkspaceType workspaceType,
                                             TestPlanLabType testPlanLabType,TestPlanResult testPlanResult,
                                             Integrations integrations,
                                             WebApplicationContext webApplicationContext)
    throws IOException, TestsigmaException, SQLException {
    WebDriverSettingsDTO webDriverSettings = new WebDriverSettingsDTO();
    List<WebDriverCapability> webDriverCapabilities = getCapabilities(testDevice, workspaceType,
        testPlanLabType, testPlanResult, integrations, webApplicationContext);
    webDriverSettings.setWebDriverCapabilities(webDriverCapabilities);
    setApplicationSpecificCapabilities(testDevice, workspaceType, testPlanResult, integrations, webDriverSettings);
    webDriverSettings.setWebDriverServerUrl(getRemoteDriverUrl(LOCAL_HOST_URL, integrations));
    return webDriverSettings;
  }

  @Override
  public void setMobileCapabilities(TestDevice testDevice, WorkspaceType workspaceType, TestPlanResult testPlanResult,
                                    Integrations integrations,
                                    WebDriverSettingsDTO webDriverSettings)
    throws TestsigmaException, MalformedURLException {
    List<WebDriverCapability> capabilities = new ArrayList<>();
    AgentDevice agentDevice = agentDeviceService.find(testDevice.getDeviceId());
    capabilities.add(new WebDriverCapability(TSCapabilityType.PLATFORM_VERSION, agentDevice.getPlatformOsVersion()));
    capabilities.add(new WebDriverCapability(TSCapabilityType.DEVICE_NAME, agentDevice.getName()));
    capabilities.add(new WebDriverCapability(TSCapabilityType.UDID, agentDevice.getUniqueId()));
    capabilities.add(new WebDriverCapability(TSCapabilityType.DEVICE_ORIENTATION, TSCapabilityType.PORTRAIT));
    capabilities.add(new WebDriverCapability(TSCapabilityType.NO_RESET, Boolean.TRUE));

    if (MobileOs.ANDROID == agentDevice.getOsName()) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.SKIP_SERVER_INSTALLATION, Boolean.TRUE));
      capabilities.add(new WebDriverCapability(TSCapabilityType.SKIP_DEVICE_INITIALIZATION, Boolean.TRUE));
    }

    if (MobileOs.IOS == agentDevice.getOsName()) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.WDA_URL, TSCapabilityType.WDA_URL_VALUE));
    }

    if (webDriverSettings.getWebDriverCapabilities() != null)
      webDriverSettings.getWebDriverCapabilities().addAll(capabilities);
    else
      webDriverSettings.setWebDriverCapabilities(capabilities);
  }

  @Override
  public void setWebCapabilities(TestDevice testDevice, TestPlanResult testPlanResult,
                                 Integrations integrations,
                                 WebDriverSettingsDTO webDriverSettings) throws MalformedURLException {
  }

  @Override
  public URL getRemoteDriverUrl(String url, Integrations integrations) throws MalformedURLException {
    return new URL("http://127.0.0.1:8080");
  }

  @Override
  public Integrations getLabDetails() throws IntegrationNotFoundException {
    return null;
  }
}
