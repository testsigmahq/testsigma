package com.testsigma.automator.actions.web.wait;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.util.Assert;

public class WaitUntilFrameIsVisibleAndSwitchToFrameAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "Successfully switched to frame which is located by \"%s:%s\".";
  private static final String FAILURE_MESSAGE = "Unable to switch to a frame, Please verify if the given " +
    "locator <b>\"%s:%s\"</b> is pointing to a valid frame. <br>Waited <b>%s</b> seconds for frame availability and trying to switch to it.";

  @Override
  public void execute() throws Exception {
    try {
      WebDriver switchedToFrame = getWebDriverWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(getBy()));
      Assert.notNull(switchedToFrame, String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()));
      setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
    } catch (TimeoutException e) {
      throw new AutomatorException(String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), getTimeout()), (Exception) e.getCause());
    }
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    if (e instanceof NoSuchElementException) {
      setErrorMessage(String.format("There is no element found with the given locator. " +
        "Please verify if the given locator <b>\"%s:%s\"</b> is pointing to a valid element/frame." +
        "Check if the frame you are looking for is inside an other frame.", getFindByType(), getLocatorValue()));
    }
  }
}
