package com.testsigma.service;


import com.testsigma.constants.TSCapabilityType;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SafariCapabilities extends Capabilities {

  @Autowired
  PlatformsService platformsService;

  @Override
  public void setTestsigmaLabCapabilities(TestDevice testDevice,
                                          Integrations integrations,
                                          List<WebDriverCapability> capabilities) {

  }

  @Override
  public void setHybridCapabilities(TestDevice testDevice,
                                    Integrations integrations,
                                    List<WebDriverCapability> capabilities) throws TestsigmaException {
    capabilities.add(new WebDriverCapability(TSCapabilityType.BROWSER_NAME, TSCapabilityType.BROWSER_NAME_SAFARI));
    PlatformOsVersion platformOsVersion = platformsService.getPlatformOsVersion(testDevice.getPlatformOsVersionId(), testDevice.getExecution().getTestPlanLabType());
    capabilities.add(new WebDriverCapability(TSCapabilityType.OS_VERSION, Platform.Mac + "" + platformOsVersion.getVersion().substring(0, 5)));
  }

}

