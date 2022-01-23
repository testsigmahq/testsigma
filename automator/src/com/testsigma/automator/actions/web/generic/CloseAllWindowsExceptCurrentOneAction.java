package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.actions.ElementAction;

import java.util.Set;

public class CloseAllWindowsExceptCurrentOneAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Closed all windows except the current one.";
  private static final String FAILURE_MESSAGE = "Unable to close browser windows/tabs.";

  @Override
  public void execute() throws Exception {
    String currWindowHandle = null;
    try {
      Set<String> windowHandles = getDriver().getWindowHandles();
      currWindowHandle = getDriver().getWindowHandle();
      for (String windowHandle : windowHandles) {
        if (!(currWindowHandle.equalsIgnoreCase(windowHandle))) {
          getDriver().switchTo().window(windowHandle).close();
        }
      }
      setSuccessMessage(SUCCESS_MESSAGE);
    } finally {
      if (currWindowHandle != null) {
        getDriver().switchTo().window(currWindowHandle);
      }
    }
  }

}
