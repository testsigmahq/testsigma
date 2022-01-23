package com.testsigma.automator.actions.common;

import com.testsigma.automator.actions.DriverAction;

public class NavigateForwardAction extends DriverAction {
  private static final String SUCCESS_MESSAGE = "Navigate forward executed successfully";

  @Override
  public void execute() throws Exception {
    getDriver().navigate().forward();
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
