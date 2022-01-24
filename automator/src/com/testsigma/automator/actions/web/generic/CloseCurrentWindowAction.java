package com.testsigma.automator.actions.web.generic;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.NoSuchSessionException;

import java.util.Set;

@Log4j2
public class CloseCurrentWindowAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Closed current window.";
  private static final String FAILURE_MESSAGE = "Unable to close the window. All Windows are closed";

  @Override
  public void execute() throws Exception {
    try {
      String currWindowHandle = getDriver().getWindowHandle();
      getDriver().switchTo().window(currWindowHandle).close();
      setSuccessMessage(SUCCESS_MESSAGE);
      //Once current window is closed, We cannot perform element/browser actions using driver. So we switch to a window if available.
      try {
        Set<String> windowHandles = getDriver().getWindowHandles();
        if (windowHandles != null) {
          for (String window : windowHandles) {
            getDriver().switchTo().window(window);
            break;
          }
        }

      } catch (Exception e) {

        log.error("Current window is closed and no other window/tab is present to switch to. " +
          "Ignore this exception as natural text action is already done", e);
        setSuccessMessage("Current window is closed and no other window/tab is present to switch to.");
        return;
      }
    } catch (NoSuchSessionException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE));
    }

  }

}
