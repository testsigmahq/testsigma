package com.testsigma.automator.actions.mobile.ios.generic;

import com.google.common.collect.ImmutableMap;

public class GoToHomeScreenAction extends com.testsigma.automator.actions.mobile.generic.GoToHomeScreenAction {

  public void pressHome() {
    getDriver().executeScript("mobile: pressButton", ImmutableMap.of("name", "home"));
  }
}

