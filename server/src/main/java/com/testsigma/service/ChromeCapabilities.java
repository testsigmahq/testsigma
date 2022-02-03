package com.testsigma.service;


import com.testsigma.constants.TSCapabilityType;
import com.testsigma.model.BrowserType;
import com.testsigma.model.Integrations;
import com.testsigma.model.TestDevice;
import com.testsigma.model.WebDriverCapability;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChromeCapabilities extends Capabilities {

  @Override
  public void setTestsigmaLabCapabilities(TestDevice testDevice,
                                          Integrations integrations,
                                          List<WebDriverCapability> capabilities) {
    capabilities.add(new WebDriverCapability(TSCapabilityType.EXTENDED_DEBUGGING, true));
    capabilities.add(new WebDriverCapability(TSCapabilityType.BROWSER_NAME, BrowserType.CHROME));
  }

  @Override
  public void setHybridCapabilities(TestDevice testDevice,
                                    Integrations integrations,
                                    List<WebDriverCapability> capabilities) {
    capabilities.add(new WebDriverCapability(TSCapabilityType.BROWSER_NAME, BrowserType.CHROME));
  }


}
