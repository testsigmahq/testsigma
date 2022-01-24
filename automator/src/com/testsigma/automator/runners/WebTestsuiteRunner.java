package com.testsigma.automator.runners;

import com.testsigma.automator.constants.DriverSessionType;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.exceptions.AutomatorException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class WebTestsuiteRunner extends TestsuiteRunner {

  public WebTestsuiteRunner() {
    super();
  }

  public void startSession(Long entityId, DriverSessionType driverSessionType) throws AutomatorException {
    DriverManager.getDriverManager(testDeviceEntity, getWorkspaceType(), testDeviceSettings.getOs(),
      entityId, driverSessionType);
  }
}
