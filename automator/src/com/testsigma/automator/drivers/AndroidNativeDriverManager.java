package com.testsigma.automator.drivers;

import com.testsigma.automator.drivers.mobile.AndroidDriver;
import com.testsigma.automator.entity.OnAbortedAction;
import com.testsigma.automator.exceptions.AutomatorException;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;

@Log4j2
public class AndroidNativeDriverManager extends DriverManager {

  AndroidNativeDriverManager() {
    super();
  }

  @Override
  public void performCleanUpAction(OnAbortedAction actionType) throws AutomatorException {
  }

  @Override
  protected RemoteWebDriver createDriverSession() throws AutomatorException, MalformedURLException {
    AndroidDriver androidDriver = new AndroidDriver();
    setDriver(androidDriver);
    return getDriver().createSession();
  }

}
