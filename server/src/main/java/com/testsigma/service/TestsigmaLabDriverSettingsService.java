package com.testsigma.service;

import com.testsigma.constants.TSCapabilityType;
import com.testsigma.dto.WebDriverSettingsDTO;
import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@Log4j2
public class TestsigmaLabDriverSettingsService extends DriverSettingsService {
  public static final String PLATFORM_WEB_URL = "%s://%s:%s@%s/wd/hub";
  public static final String PLATFORM_WEB_URL_WITH_PORT = "%s://%s:%s@%s:%s/wd/hub";
  public static final String PLATFORM_MOBILE_URL = "%s://%s:%s@%s/mobile/wd/hub";
  public static final String PLATFORM_MOBILE_URL_WITH_PORT = "%s://%s:%s@%s:%s/mobile/wd/hub";



  @Autowired
  PlatformsService platformsService;
  @Autowired
  private IntegrationsService integrationsService;
  @Autowired
  private TestsigmaOSConfigService testsigmaOSConfigService;

  @Override
  public WebDriverSettingsDTO driverSettings(TestDevice testDevice, WorkspaceType workspaceType,
                                             TestPlanLabType testPlanLabType,TestPlanResult testPlanResult,
                                             Integrations integrations,
                                             WebApplicationContext webApplicationContext)
    throws IOException, TestsigmaException, SQLException {
    return super.driverSettings(testDevice, workspaceType, testPlanLabType, testPlanResult, integrations,
      webApplicationContext);
  }


  @Override
  public URL getRemoteDriverUrl(String url, Integrations integrations)
    throws MalformedURLException {
    URL testsigmaPlatformUrl = new URL(testsigmaOSConfigService.getUrl());
    URL remoteDriverURL;
    if (testsigmaPlatformUrl.getPort() > 0) {
      remoteDriverURL = new URL(String.format(url, testsigmaPlatformUrl.getProtocol(),
        integrations.getUsername(), integrations.getPassword(),
        testsigmaPlatformUrl.getHost(), testsigmaPlatformUrl.getPort()));
    } else {
      remoteDriverURL = new URL(String.format(url, testsigmaPlatformUrl.getProtocol(),
        integrations.getUsername(), integrations.getPassword(),
        testsigmaPlatformUrl.getHost()));
    }

    log.info("Generated Remote Driver URL - " + remoteDriverURL);
    return remoteDriverURL;
  }

  @Override
  public void setMobileCapabilities(TestDevice testDevice, WorkspaceType workspaceType, TestPlanResult testPlanResult,
                                    Integrations integrations,
                                    WebDriverSettingsDTO webDriverSettings)
    throws TestsigmaException, MalformedURLException {
    List<WebDriverCapability> capabilities = new ArrayList<>();
    PlatformDevice device = platformsService.getPlatformDevice(testDevice.getPlatformDeviceId(), testDevice.getTestPlanLabType());
    PlatformOsVersion platformOsVersion = platformsService.getPlatformOsVersion(testDevice.getPlatformOsVersionId(),testDevice.getTestPlanLabType());
    Platform os = device.getPlatform();
    HashMap<String, Object> tsLabOptions = new HashMap<String, Object>();
    capabilities.add(new WebDriverCapability(TSCapabilityType.DEVICE_NAME, device.getName() == null ? device.getDisplayName() : device.getName()));
    capabilities.add(new WebDriverCapability(TSCapabilityType.PLATFORM_VERSION, platformOsVersion.getPlatformVersion()));
    if (Platform.Android.equals(os)) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.AUTOMATION_NAME, TSCapabilityType.UI_AUTOMATOR));
    } else if (Platform.iOS.equals(os)) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.AUTOMATION_NAME, TSCapabilityType.XCUI_TEST));
    }
    if (WorkspaceType.MobileWeb.equals(workspaceType)) {
      PlatformBrowserVersion platformBrowserVersion = platformsService.getPlatformBrowserVersion(testDevice.getPlatformBrowserVersionId(), testDevice.getTestPlanLabType());
      capabilities.add(new WebDriverCapability(TSCapabilityType.BROWSER_NAME, platformBrowserVersion.getName()));
    }
    capabilities.add(new WebDriverCapability(TSCapabilityType.TESTSIGMA_LAB_NEW_COMMAND_TIMEOUT_CAP,
      TSCapabilityType.TESTSIGMA_LAB_NEW_COMMAND_TIMEOUT_VAL));
    tsLabOptions.put(TSCapabilityType.NAME,testPlanResult.getTestPlan().getName());
    tsLabOptions.put(TSCapabilityType.DEVICE_ORIENTATION, TSCapabilityType.PORTRAIT);
    capabilities.add(new WebDriverCapability(TSCapabilityType.TESTSIGMA_LAB_OPTIONS,tsLabOptions));
    if (webDriverSettings.getWebDriverCapabilities() != null)
      webDriverSettings.getWebDriverCapabilities().addAll(capabilities);
    else
      webDriverSettings.setWebDriverCapabilities(capabilities);
    if (new URL(testsigmaOSConfigService.getUrl()).getPort() > 0) {
      webDriverSettings.setWebDriverServerUrl(getRemoteDriverUrl(PLATFORM_MOBILE_URL_WITH_PORT, integrations));
    } else {
      webDriverSettings.setWebDriverServerUrl(getRemoteDriverUrl(PLATFORM_MOBILE_URL, integrations));
    }
  }

  @Override
  public void setWebCapabilities(TestDevice testDevice,TestPlanResult testPlanResult,
                                 Integrations integrations,
                                 WebDriverSettingsDTO webDriverSettings)
    throws MalformedURLException, TestsigmaException {
    List<WebDriverCapability> capabilities = new ArrayList<>();

    PlatformOsVersion platformOsVersion = platformsService.getPlatformOsVersion(testDevice.getPlatformOsVersionId(), testDevice.getTestPlanLabType());
    PlatformBrowserVersion platformBrowserVersion = platformsService.getPlatformBrowserVersion(testDevice.getPlatformBrowserVersionId(), testDevice.getTestPlanLabType());
    PlatformScreenResolution platformScreenResolution = platformsService.getPlatformScreenResolution(testDevice.getPlatformScreenResolutionId(), testDevice.getTestPlanLabType());
    HashMap<String, Object> tsLabOptions = new HashMap<String, Object>();
    capabilities.add(new WebDriverCapability(TSCapabilityType.PLATFORM_NAME, platformOsVersion.getPlatformVersion()));
    capabilities.add(new WebDriverCapability(TSCapabilityType.VERSION, platformBrowserVersion.getVersion()));
    String resolution = platformScreenResolution.getResolution();
    if (!StringUtils.isBlank(resolution)) {
      tsLabOptions.put(TSCapabilityType.TESTSIGMA_LAB_KEY_SCREEN_RESOLUTION, resolution);
    } else {
      tsLabOptions.put(TSCapabilityType.TESTSIGMA_LAB_KEY_SCREEN_RESOLUTION,
              TSCapabilityType.DEFAULT_RESOLUTION);
    }
    tsLabOptions.put(TSCapabilityType.NAME,testPlanResult.getTestPlan().getName());
    tsLabOptions.put(TSCapabilityType.OS, platformOsVersion.getPlatform());
    tsLabOptions.put(TSCapabilityType.OS_VERSION, platformOsVersion.getPlatformVersion());
    tsLabOptions.put(TSCapabilityType.KEY_MAX_IDLE_TIME, TSCapabilityType.MAX_IDLE_TIME);
    tsLabOptions.put(TSCapabilityType.KEY_MAX_DURATION, TSCapabilityType.MAX_DURATION);
    capabilities.add(new WebDriverCapability(TSCapabilityType.TESTSIGMA_LAB_OPTIONS,tsLabOptions));
    if (webDriverSettings.getWebDriverCapabilities() != null)
      webDriverSettings.getWebDriverCapabilities().addAll(capabilities);
    else
      webDriverSettings.setWebDriverCapabilities(capabilities);
    if (new URL(testsigmaOSConfigService.getUrl()).getPort() > 0) {
      webDriverSettings.setWebDriverServerUrl(getRemoteDriverUrl(PLATFORM_WEB_URL_WITH_PORT, integrations));
    } else {
      webDriverSettings.setWebDriverServerUrl(getRemoteDriverUrl(PLATFORM_WEB_URL, integrations));
    }
  }

  @Override
  public Integrations getLabDetails() throws IntegrationNotFoundException {
    return this.integrationsService.findByApplication(Integration.TestsigmaLab);
  }

}
