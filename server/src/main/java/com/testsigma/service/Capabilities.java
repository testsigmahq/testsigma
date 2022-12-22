package com.testsigma.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.Integrations;
import com.testsigma.model.TestDevice;
import com.testsigma.model.WebDriverCapability;
import com.testsigma.sdk.ApplicationType;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public abstract class Capabilities {

  public List<WebDriverCapability> getCapabilities(TestDevice testDevice,
                                                   Integrations integrations,
                                                   TestPlanLabType testPlanLabType)
    throws TestsigmaException, IOException {
    List<WebDriverCapability> capabilities = new ArrayList<>();
    setDesiredCapabilities(testDevice, capabilities);
    setPlatformSpecificCapabilities(testDevice, testPlanLabType, integrations, capabilities);
    return capabilities;
  }

  public void setDesiredCapabilities(TestDevice testDevice, List<WebDriverCapability> capabilities)
    throws IOException {
    if (testDevice.getCapabilities() != null) {
      String capabilityStr = testDevice.getCapabilities();
      List<Map<String, Object>> additionalCapabilitiesList =
        new ObjectMapperService().parseJson(capabilityStr, new TypeReference<>() {
        });
      for (Map<String, Object> capability : additionalCapabilitiesList) {
        String name = capability.get("name").toString();
        if (!name.equals(""))
          capabilities.add(new WebDriverCapability(name, capability.get("value")));
      }
    }
  }

  protected void setPlatformSpecificCapabilities(TestDevice testDevice,
                                                 TestPlanLabType testPlanLabType,
                                                 Integrations integrations,
                                                 List<WebDriverCapability> capabilities)
    throws TestsigmaException {
    switch (testPlanLabType) {
      case TestsigmaLab:
      case PrivateGrid:
        setTestsigmaLabCapabilities(testDevice, integrations, capabilities);
        break;
      case Hybrid:
        setHybridCapabilities(testDevice, integrations, capabilities, testPlanLabType);
        break;
      default:
        log.error("Unsupported execution lab type - " + testPlanLabType);
    }
  }

  public abstract void setHybridCapabilities(TestDevice testDevice,
                                             Integrations integrations,
                                             List<WebDriverCapability> capabilities,
                                             TestPlanLabType testPlanLabType)
    throws TestsigmaException;

  public abstract void setTestsigmaLabCapabilities(TestDevice testDevice,
                                                   Integrations integrations,
                                                   List<WebDriverCapability> capabilities) throws TestsigmaException;

}
