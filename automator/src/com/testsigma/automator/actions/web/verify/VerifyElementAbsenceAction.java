package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.ElementAction;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.springframework.util.Assert;

@Log4j2
public class VerifyElementAbsenceAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "The element corresponding to the locator " +
    "<b>\"%s:%s\"</b> is not available as expected";
  private static final String FAILURE_MESSAGE = "The element corresponding to the locator  " +
    "<b>\"%s:%s\"</b> is available in this page, which is not expected.";

  @Override
  public void execute() throws Exception {
    boolean elementFound = false;
    try {
      findElement();
      if (getElement().isDisplayed()) {
        elementFound = true;
      }

    } catch (Exception e) {
      log.debug("Expected exception(Verify element not present):", e);
      setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
      return;
    }
    Assert.isTrue(Boolean.FALSE.equals(elementFound), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getFindByType(), getLocatorValue()));
  }
}
