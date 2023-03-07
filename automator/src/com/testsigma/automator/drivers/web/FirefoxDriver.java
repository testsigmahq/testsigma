package com.testsigma.automator.drivers.web;

import com.testsigma.automator.constants.EnvSettingsConstants;
import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.utilities.PathUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class FirefoxDriver extends WebDriver {
  FirefoxOptions firefoxOptions;

  public FirefoxDriver() {
    super();
    firefoxOptions = new FirefoxOptions();
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    if (remoteServerURL != null) {

      remoteWebDriver = new RemoteWebDriver(remoteServerURL, firefoxOptions.merge(desiredCapabilities));
    } else {
      remoteWebDriver = new org.openqa.selenium.firefox.FirefoxDriver(new FirefoxOptions().merge(desiredCapabilities));
    }
  }

  @Override
  public void setTestsigmaLabCapabilities() throws AutomatorException {
    if (!"Linux".equals(getPlatform())) {
      MutableCapabilities mutableCapabilities = new MutableCapabilities();
      mutableCapabilities.setCapability(TSCapabilityType.SELENIUM_VERSION, "3.8.1");
      mutableCapabilities.setCapability(TSCapabilityType.NAME, executionName);
      mutableCapabilities.setCapability(EnvSettingsConstants.KEY_MAX_IDLE_TIME, EnvSettingsConstants.MAX_IDLE_TIME);
      mutableCapabilities.setCapability(EnvSettingsConstants.KEY_MAX_DURATION, EnvSettingsConstants.MAX_DURATION);
      mutableCapabilities.setCapability("username", this.getTestDeviceEntity().getUsername());
      mutableCapabilities.setCapability("accessKey", this.getTestDeviceEntity().getAccessKey());
      capabilities.add(new WebDriverCapability(TSCapabilityType.TESTSIGMA_LAB_OPTIONS, mutableCapabilities));
      setTestsigmaLabRemoteServerUrl();
    } else {
      super.setTestsigmaLabCapabilities();
    }
    capabilities.add(new WebDriverCapability(CapabilityType.BROWSER_NAME, Browser.FIREFOX));
  }

  protected void setAdditionalCapabilities(List<WebDriverCapability> additionalCapabilitiesList) {
    if (additionalCapabilitiesList != null) {
      for (WebDriverCapability capability : additionalCapabilitiesList) {
        String name = capability.getCapabilityName();
        capabilities.add(new WebDriverCapability(name, capability.getCapabilityValue()));
      }
    }
  }


  @Override
  public void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
    System.setProperty(TSCapabilityType.BROWSER_DRIVER_PROPERTY_FIREFOX,
      PathUtil.getInstance().getDriversPath() + settings.getHybridBrowserDriverPath());
  }

  @Override
  protected void setBrowserSpecificCapabilities(List<WebDriverCapability> additionalCapabilitiesList) throws AutomatorException {
    if (additionalCapabilitiesList != null) {
      for (Iterator<WebDriverCapability> single = additionalCapabilitiesList.listIterator(); single.hasNext(); ) {
        WebDriverCapability singleCap = single.next();
        String name = singleCap.getCapabilityName();

        if (com.testsigma.automator.constants.DesiredCapabilities.FIREFOXPROFILE.equals(name)) {
          FirefoxProfile profile = new FirefoxProfile();
          String values = singleCap.getCapabilityValue().toString();
          Map<String, Object> profiles = parseCapabilities(values, TSCapabilityType.FIREFOX_PROFILE);
          for (Map.Entry<String, Object> pro : profiles.entrySet()) {
            if (pro.getValue() instanceof Boolean) {
              profile.setPreference(pro.getKey(), (Boolean) pro.getValue());
            } else if (pro.getValue() instanceof Integer) {
              profile.setPreference(pro.getKey(), (Integer) pro.getValue());
            } else {
              profile.setPreference(pro.getKey(), (String) pro.getValue());
            }
          }
          capabilities.add(new WebDriverCapability(org.openqa.selenium.firefox.FirefoxDriver.SystemProperty.BROWSER_PROFILE, profile));
          single.remove();
        }
      }
    }
  }

  @Override
  protected void setTimeouts() throws AutomatorException {
    if ("Linux".equals(getPlatform())) {
      super.setTimeouts();
    }
  }

}
