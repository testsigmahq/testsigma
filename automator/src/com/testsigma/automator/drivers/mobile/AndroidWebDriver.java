package com.testsigma.automator.drivers.mobile;

import com.testsigma.automator.constants.TSCapabilityType;
import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.exceptions.AutomatorException;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;

@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class AndroidWebDriver extends MobileWebDriver {

  public AndroidWebDriver() {
    super();
  }

  @Override
  protected void setHybridCapabilities() throws AutomatorException, MalformedURLException {
    super.setHybridCapabilities();
    ChromeOptions options = new ChromeOptions();
    options.setExperimentalOption("w3c", false);
    capabilities.add(new WebDriverCapability("nativeWebScreenshot", Boolean.TRUE));

    if (settings.getChromedriverExecutableDir() != null) {
      capabilities.add(new WebDriverCapability(TSCapabilityType.CHROME_DRIVER_EXECUTABLE_DIR,
        settings.getChromedriverExecutableDir()));
    }
    capabilities.add(new WebDriverCapability(ChromeOptions.CAPABILITY, options));
    capabilities.add(new WebDriverCapability("automationName","uiAutomator2"));
    capabilities.add(new WebDriverCapability(AndroidMobileCapabilityType.UNICODE_KEYBOARD, Boolean.TRUE));
    capabilities.add(new WebDriverCapability(AndroidMobileCapabilityType.RESET_KEYBOARD, Boolean.TRUE));
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    remoteWebDriver = new io.appium.java_client.android.AndroidDriver(remoteServerURL, desiredCapabilities);
  }
}
