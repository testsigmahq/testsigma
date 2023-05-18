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
import java.util.List;

@Service
@Log4j2
public class PrivateGridDriverSettingsService extends DriverSettingsService {
  public static final String PLATFORM_WEB_URL = "/wd/hub";

  @Autowired
  PlatformsService platformsService;
  @Autowired
  private IntegrationsService integrationsService;
  @Autowired
  private TestsigmaOSConfigService testsigmaOSConfigService;

  @Override
  public WebDriverSettingsDTO driverSettings(TestDevice testDevice, WorkspaceType workspaceType,
                                             TestPlanLabType testPlanLabType, TestPlanResult testPlanResult,
                                             Integrations integrations,
                                             WebApplicationContext webApplicationContext)
          throws IOException, TestsigmaException, SQLException {
    return super.driverSettings(testDevice, workspaceType, testPlanLabType, testPlanResult, integrations,
            webApplicationContext);
  }


  @Override
  public URL getRemoteDriverUrl(String url, Integrations integrations)
          throws MalformedURLException {
    URL gridURL = new URL(integrations.getUrl());
    URL remoteDriverURL;
    if(integrations.getUrl().charAt(integrations.getUrl().length()-1)== '/')
      remoteDriverURL = new URL(gridURL + url.substring(1) );
    else remoteDriverURL = new URL(gridURL + url);
    log.info("Generated Remote Driver URL - " + remoteDriverURL);
    return remoteDriverURL;
  }


  @Override
  public void setWebCapabilities(TestDevice testDevice, TestPlanResult testPlanResult,
                                 Integrations integrations,
                                 WebDriverSettingsDTO webDriverSettings)
          throws MalformedURLException, TestsigmaException {
    List<WebDriverCapability> capabilities = new ArrayList<>();

  /*  PlatformOsVersion platformOsVersion = platformsService.getPlatformOsVersion(testDevice.getPlatformOsVersionId(), testDevice.getTestPlan().getTestPlanLabType());
    PlatformBrowserVersion platformBrowserVersion = platformsService.getPlatformBrowserVersion(testDevice.getPlatformBrowserVersionId(), testDevice.getTestPlan().getTestPlanLabType());
    PlatformScreenResolution platformScreenResolution = platformsService.getPlatformScreenResolution(testDevice.getPlatformScreenResolutionId(), testDevice.getTestPlan().getTestPlanLabType());
    */

    capabilities.add(new WebDriverCapability(TSCapabilityType.PLATFORM, testDevice.getPlatform()));
    if ((testDevice.getBrowserVersion()!=null && !testDevice.getBrowserVersion().equals("Not Available")))
    capabilities.add(new WebDriverCapability(TSCapabilityType.VERSION, testDevice.getBrowserVersion()));
    Browsers browser = Browsers.getBrowser(testDevice.getBrowser());
      capabilities.add(new WebDriverCapability(TSCapabilityType.TESTSIGMA_LAB_KEY_SCREEN_RESOLUTION, TSCapabilityType.DEFAULT_RESOLUTION));
    capabilities.add(new WebDriverCapability(TSCapabilityType.KEY_MAX_IDLE_TIME, TSCapabilityType.MAX_IDLE_TIME));
    capabilities.add(new WebDriverCapability(TSCapabilityType.KEY_MAX_DURATION, TSCapabilityType.MAX_DURATION));
    if (webDriverSettings.getWebDriverCapabilities() != null)
      webDriverSettings.getWebDriverCapabilities().addAll(capabilities);
    else
      webDriverSettings.setWebDriverCapabilities(capabilities);
      webDriverSettings.setWebDriverServerUrl(getRemoteDriverUrl(PLATFORM_WEB_URL, integrations));

  }

  @Override
  public Integrations getLabDetails() throws IntegrationNotFoundException {
    return this.integrationsService.findByApplication(Integration.PrivateGrid);
  }

  @Override
  public void setMobileCapabilities(TestDevice testDevice, WorkspaceType workspaceType, TestPlanResult testPlanResult, Integrations integrations, WebDriverSettingsDTO webDriverSettings) throws TestsigmaException, MalformedURLException {

  }
}
