package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;

import java.util.Set;

public class CloseAllWindowsAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Closed all windows.";

  @Override
  public void execute() throws Exception {
    Set<String> windowHandles = getDriver().getWindowHandles();
    for (String windowHandle : windowHandles) {
      getDriver().switchTo().window(windowHandle).close();
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
