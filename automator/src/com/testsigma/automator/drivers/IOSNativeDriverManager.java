package com.testsigma.automator.drivers;

import com.testsigma.automator.drivers.mobile.IosDriver;
import com.testsigma.automator.entity.OnAbortedAction;
import com.testsigma.automator.exceptions.AutomatorException;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;

@Log4j2
public class IOSNativeDriverManager extends DriverManager {

  IOSNativeDriverManager() {
    super();
  }

  @Override
  public void performCleanUpAction(OnAbortedAction actionType) throws AutomatorException {
  }

  @Override
  protected RemoteWebDriver createDriverSession() throws AutomatorException, MalformedURLException {
    IosDriver iosDriver = new IosDriver();
    setDriver(iosDriver);
    return getDriver().createSession();
  }
}
