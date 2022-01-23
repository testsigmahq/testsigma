package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.CustomExpectedConditions;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

@Log4j2
public class WaitUntilNewWindowIsPresentAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully waited until a new window is opened/present.";
  private static final String FAILURE_MESSAGE = "New window did not opened in the given wait time," +
    " Waited <b>%s</b> seconds for new window to open.<br> If the new window has to open upon clicking a link, you can use " +
    " below Action to verify it.<br>\"Verify that the link (element) opens in a new window or tab\"";

  @Override
  public void execute() throws Exception {
    try {
      int currentWindows = getDriver().getWindowHandles().size();
      log.info("No of windows present:" + currentWindows);
      log.info("Started polling for new window.");
      boolean newWindowIsPresent = getWebDriverWait().until(CustomExpectedConditions.newWindowtobePresent(currentWindows));
      Assert.isTrue(newWindowIsPresent, String.format(FAILURE_MESSAGE, getTimeout()));
      setSuccessMessage(SUCCESS_MESSAGE);
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getTimeout()), (Exception) e.getCause());
    }
  }
}
