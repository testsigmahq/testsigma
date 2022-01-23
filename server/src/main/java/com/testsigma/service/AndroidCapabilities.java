package com.testsigma.service;

import com.testsigma.constants.TSCapabilityType;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class AndroidCapabilities extends MobileCapabilities {

  @Override
  public void setTestsigmaLabCapabilities(TestDevice testDevice,
                                          Integrations integrations,
                                          List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    capabilities.add(new WebDriverCapability(TSCapabilityType.PLATFORM_NAME, Platform.Android.name()));
    if (testDevice.getAppPathType() != null)
      setTestsigmaLabAppCapability(testDevice, testDevice.getAppPathType(),
              integrations, capabilities);
  }

  public void setHybridAppCapability(TestDevice testDevice, AppPathType appPathType,
                                     List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    if (AppPathType.UPLOADS == appPathType) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.APP, getPreSignedUrl(testDevice)));
    } else if (AppPathType.USE_PATH == appPathType) {
      if (testDevice.getAppUrl() != null) {
        capabilities.add(new WebDriverCapability(TSCapabilityType.APP, testDevice.getAppUrl()));
      } else {
        String preSignedUrl = getPreSignedUrl(testDevice);
        capabilities.add(new WebDriverCapability(TSCapabilityType.APP, preSignedUrl));
      }
    } else if (AppPathType.APP_DETAILS == appPathType) {
      capabilities.add(new WebDriverCapability("appPackage",
        testDevice.getAppPackage()));
      capabilities.add(new WebDriverCapability("appActivity",
        testDevice.getAppActivity()));
    }
  }

  @Override
  public void setHybridCapabilities(TestDevice testDevice,
                                    Integrations integrations,
                                    List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    capabilities.add(new WebDriverCapability(TSCapabilityType.PLATFORM_NAME, Platform.Android.name()));
    capabilities.add(new WebDriverCapability("automationName", "UiAutomator2"));
    if (testDevice.getAppPathType() != null)
      setHybridAppCapability(testDevice, testDevice.getAppPathType(),
        capabilities);
  }
}
