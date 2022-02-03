package com.testsigma.automator.drivers.web;

import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.TestsigmaDriver;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.entity.ExecutionLabType;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.utilities.TimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.html5.LocationContext;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class WebDriver extends TestsigmaDriver {

  private static final String INVALID_GEO_LOCATION_ERROR = "Invalid \"geolocation\" desired capability value provided " +
    "in the execution configuration. For correct format refer to " +
    "https://testsigma.freshdesk.com/solution/articles/32000024808-sample-desired-capabilities";

  protected WebDriverCapability locationCapability;

  public WebDriver() {
    super();
  }

  @Override
  protected void setCapabilities() throws AutomatorException, MalformedURLException {
    super.setCapabilities();
    List<WebDriverCapability> additionalCapabilitiesList = webDriverSettings.getWebDriverCapabilities();
    setCommonCapabilities();
    setPlatformSpecificCapabilities();
    checkForLocationCapability(additionalCapabilitiesList);
    setBrowserSpecificCapabilities(additionalCapabilitiesList);
    setAdditionalCapabilities(additionalCapabilitiesList);
  }

  @Override
  protected void setCommonCapabilities() throws AutomatorException {
    super.setCommonCapabilities();
    capabilities.add(new WebDriverCapability(TSCapabilityType.NAME, executionName));
    capabilities.add(new WebDriverCapability(TSCapabilityType.ACCEPT_SSL_CERTS, Boolean.TRUE));
    capabilities.add(new WebDriverCapability(TSCapabilityType.UNHANDLED_PROMPT_BEHAVIOUR_KEY, TSCapabilityType.UNHANDLED_PROMPT_BEHAVIOUR_VALUE));
  }

  @Override
  protected void setTestsigmaLabCapabilities() throws AutomatorException {
    super.setTestsigmaLabCapabilities();
  }


  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
  }

  protected void setBrowserSpecificCapabilities(List<WebDriverCapability> additionalCapabilitiesList)
    throws AutomatorException {
  }


  protected void checkForLocationCapability(List<WebDriverCapability> additionalCapabilitiesList) {
    if (additionalCapabilitiesList != null) {
      for (Iterator<WebDriverCapability> single = additionalCapabilitiesList.listIterator(); single.hasNext(); ) {
        WebDriverCapability capability = single.next();
        String name = capability.getCapabilityName();

        if (com.testsigma.automator.constants.DesiredCapabilities.GEOLOCATION.equals(name)) {
          single.remove();
          locationCapability = new WebDriverCapability(name, capability.getCapabilityValue());
        }
      }
    }
  }

  @Override
  protected RemoteWebDriver createDriver(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    Instant start = Instant.now();
    createDriverInstance(desiredCapabilities);
    Instant end = Instant.now();

    log.info("Web Driver Session Created in - " + TimeUtil.getFormattedDuration(start, end));
    log.info("Stating with post web driver creation actions");

    setLocation();
    setFileDetector();
    //deleteAllCookies();
    maximizeWindow();
    setTimeouts();

    log.info("Finished post web driver creation actions in ");
    return remoteWebDriver;
  }

  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
  }

  protected void maximizeWindow() {
    getRemoteWebDriver().manage().window().maximize();
  }

  protected void setFileDetector() {
    if (getExecutionLabType() != ExecutionLabType.Hybrid) {
      remoteWebDriver.setFileDetector(new LocalFileDetector());
    }
  }

  protected void setLocation() throws AutomatorException {
    if (locationCapability != null) {
      try {
        String[] coordinates = locationCapability.getCapabilityValue().toString().split(",");
        ((LocationContext) new Augmenter().augment(getRemoteWebDriver()))
          .setLocation(new Location(Double.parseDouble(coordinates[0]),
            Double.parseDouble(coordinates[1]),
            Double.parseDouble(coordinates[2])));
      } catch (NumberFormatException e) {
        throw new AutomatorException(INVALID_GEO_LOCATION_ERROR);
      }
    }
  }

  protected String getSafariVersion() {
    String userAgent = (String) remoteWebDriver.executeScript("return navigator.userAgent;");
    return userAgent.substring(userAgent.indexOf("Version") + 8, userAgent.indexOf("Safari") - 1);
  }
}
