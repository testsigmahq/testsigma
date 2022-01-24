package com.testsigma.automator.drivers;

import com.testsigma.automator.drivers.mobile.IosWebDriver;
import com.testsigma.automator.entity.OnAbortedAction;
import com.testsigma.automator.exceptions.AutomatorException;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;

@Log4j2
public class IOSWebDriverManager extends DriverManager {

  IOSWebDriverManager() {
    super();
  }

  @Override
  public void performCleanUpAction(OnAbortedAction actionType) throws AutomatorException {
  }

  @Override
  protected RemoteWebDriver createDriverSession() throws AutomatorException, MalformedURLException {
    IosWebDriver iosWebDriver = new IosWebDriver();
    setDriver(iosWebDriver);
    return getDriver().createSession();
  }
}
