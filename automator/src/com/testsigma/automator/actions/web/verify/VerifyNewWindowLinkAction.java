package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import org.springframework.util.Assert;

import java.util.Set;

public class VerifyNewWindowLinkAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified that the link is opening in a new window or tab";
  private static final String FAILURE_MESSAGE = "The link corresponding to the locator <b>\"%s:%s\"</b>" +
    " is not opening in new tab/window";

  @Override
  protected void execute() throws Exception {
    Set<String> getWindowHandlesBeforeClick = getDriver().getWindowHandles();
    findElement();
    getElement().click();
    Set<String> getWindowHandlesAfterClick = getDriver().getWindowHandles();
    Assert.isTrue((getWindowHandlesAfterClick.size() > getWindowHandlesBeforeClick.size()), String.format(FAILURE_MESSAGE,
      getFindByType(), getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

}
