package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.exceptions.ElementNotDisplayedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

@Log4j2
public class VerifyElementNotDisplayedAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the element is not displayed";
  private static final String FAILURE_MESSAGE = "The element corresponding to the locator <b>\"%s:%s\"</b> is displayed, which is not expected.";

  @Override
  public void execute() throws Exception {
    try {
      findElement();
    } catch (ElementNotDisplayedException e) {
      log.info("Element is not displayed as expected.");
      setSuccessMessage(SUCCESS_MESSAGE);
      return;
    }
    Assert.isTrue(!(getElement().isDisplayed()), String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue()));
  }
}
