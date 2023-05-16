package com.testsigma.automator.drivers.web;

import com.google.gson.JsonObject;
import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.utilities.PathUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class EdgeDriver extends WebDriver {

  public EdgeDriver() {
    super();
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    if (remoteServerURL != null) {
      remoteWebDriver = new RemoteWebDriver(remoteServerURL, new EdgeOptions().merge(desiredCapabilities));
    } else {
      remoteWebDriver = new org.openqa.selenium.edge.EdgeDriver(new EdgeOptions().merge(desiredCapabilities));
    }
  }

  @Override
  protected void setTestsigmaLabCapabilities() throws AutomatorException {
    super.setTestsigmaLabCapabilities();
  }

  @Override
  public void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
    System.setProperty(TSCapabilityType.BROWSER_DRIVER_PROPERTY_EDGE,
      PathUtil.getInstance().getDriversPath() + settings.getHybridBrowserDriverPath());
  }

  @Override
  protected void setBrowserSpecificCapabilities(List<WebDriverCapability> additionalCapabilitiesList) throws AutomatorException {
    JSONObject proxyOptions=new JSONObject();
    proxyOptions.put("proxyType","system");
    capabilities.add(new WebDriverCapability(TSCapabilityType.AVOID_PROXY, proxyOptions));
  }
}
