package com.testsigma.automator.actions.web.enter;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

public abstract class EnterAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully entered data \"%s\" into the element with locator \"%s:%s\"";
  private static final String FAILURE_MESSAGE_NODATA = "Found the element, But unable to enter given test data <b>\"%s\"</b>";
  private static final String ELEMENT_NOT_FOUND = "Unable to find a element with locator <b>\"%s:%s\"</b>";

  protected void enter() throws Exception {
    findElement();
    getElement().sendKeys(getTestData());
    Assert.notNull(getElement().getText(), getEnterFailedMessage());
    setSuccessMessage(getSelectSucceededMessage());
  }

  @Override
  protected void handleException(Exception e) {
    super.handleException(e);
    updateErrorMessageForDynamicLocatorTypes(e, getElementNotFoundMessage());
  }

  protected String getSelectSucceededMessage() {
    return String.format(SUCCESS_MESSAGE, getTestData(), getFindByType(), getLocatorValue());
  }

  protected String getEnterFailedMessage() {
    return String.format(FAILURE_MESSAGE_NODATA, getTestData());
  }

  protected String getElementNotFoundMessage() {
    return String.format(ELEMENT_NOT_FOUND, getFindByType(), getLocatorValue());
  }
}
