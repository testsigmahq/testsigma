package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;


public class VerifyElementTextAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the element is displaying expected text";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "Display Text for element corresponding to the locator <br>\"%s:" +
    "%s\"</br> is not available.";
  private static final String FAILURE_MESSAGE_MISMATCH = "The display text for element corresponding to the locator <br>\"%s:" +
    "%s\"</br> is not as expected.<br>Expected:\"%s\"<br>Actual:\"%s\"";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getText());
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    Assert.isTrue(getActualValue().toString().equals(getTestData()), String.format(FAILURE_MESSAGE_MISMATCH,
      getFindByType(), getLocatorValue(), getTestDataMaskResult(), getActualValue().toString()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
