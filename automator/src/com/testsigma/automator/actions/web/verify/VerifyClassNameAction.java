/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.web.verify;

import com.testsigma.automator.actions.ElementAction;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class VerifyClassNameAction extends ElementAction {
  private static final String FAILURE_MESSAGE = "The class attribute of the element corresponding to the locator " +
    "<b>\"%s:%s\"</b> is not same as expected.<br>Unavailable class name:\"%s\"<br>Expected: \"%s\" <br>Actual:\"%s\"";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The class attribute of the element corresponding to the locator " +
    "<b>\"%s:%s\"</b> is not available";
  private static final String SUCCESS_MESSAGE = "Successfully verified class name";

  @Override
  public void execute() throws Exception {
    findElement();
    setActualValue(getElement().getAttribute(ActionConstants.ATTRIBUTE_CLASS));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(), getLocatorValue()));
    List<String> actualClassNames = Arrays.asList(getActualValue().toString().split(" "));
    String[] expectedClassNames = getTestData().split(" ");
    for (String expectedClassName : expectedClassNames) {
      Assert.isTrue(actualClassNames.contains(expectedClassName),
        String.format(FAILURE_MESSAGE, getFindByType(), getLocatorValue(), expectedClassName, getTestData(), getActualValue()));
    }
    setSuccessMessage(SUCCESS_MESSAGE);
  }
}
