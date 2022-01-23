package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class VerifyElementInnerTextNotEmptyAction extends ElementAction {
  private static final String SUCCESS_MESSAGE = "Successfully verified inner text of element";
  private static final String FAILURE_MESSAGE = "Inner text of element corresponding to the locator <b>\"%s:%s\"</b> is empty";

  @Override
  protected void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(ActionConstants.INNER_HTML));
    Assert.isTrue(StringUtils.isNotBlank((String) getActualValue()), String.format(FAILURE_MESSAGE, getFindByType(),
      getLocatorValue()));
    setSuccessMessage(SUCCESS_MESSAGE);
  }

}
