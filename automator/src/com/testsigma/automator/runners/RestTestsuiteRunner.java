package com.testsigma.automator.runners;

import com.testsigma.automator.constants.DriverSessionType;
import com.testsigma.automator.exceptions.AutomatorException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RestTestsuiteRunner extends TestsuiteRunner {
  public RestTestsuiteRunner() {
    super();
  }

  public void startSession(Long entityId, DriverSessionType driverSessionType) throws AutomatorException {
  }

  public void endSession() throws AutomatorException {
  }
}
