package com.testsigma.automator.drivers.mobile;

import com.testsigma.automator.drivers.WebDriverCapability;
import com.testsigma.automator.entity.Platform;
import com.testsigma.automator.exceptions.AutomatorException;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.DesiredCapabilities;


@EqualsAndHashCode(callSuper = true)
@Data
@Log4j2
public class IosWebDriver extends MobileWebDriver {

  public IosWebDriver() {
    super();
  }

  @Override
  protected void setCommonCapabilities() throws AutomatorException {
    super.setCommonCapabilities();
    capabilities.add(new WebDriverCapability("automationName","XCUITest"));
    capabilities.add(new WebDriverCapability(MobileCapabilityType.PLATFORM_NAME, Platform.iOS.name()));
  }

  @Override
  protected void createDriverInstance(DesiredCapabilities desiredCapabilities) throws AutomatorException {
    remoteWebDriver = new IOSDriver(getRemoteServerURL(), desiredCapabilities);
  }
}
