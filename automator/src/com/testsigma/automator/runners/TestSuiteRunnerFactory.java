package com.testsigma.automator.runners;

import com.testsigma.automator.entity.TestDeviceEntity;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class TestSuiteRunnerFactory {

  public TestsuiteRunner getRunner() {
    TestDeviceEntity testDeviceEntity = EnvironmentRunner.getRunnerEnvironmentEntity();
    switch (testDeviceEntity.getWorkspaceType()) {
      case Rest:
        return new RestTestsuiteRunner();
      default:
        return new WebTestsuiteRunner();
    }
  }
}
