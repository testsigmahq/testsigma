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
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;

@Log4j2
public class VerifyCssValueAction extends ElementAction {

  private static final String SUCCESS_MESSAGE = "The CSS value for the property \"%s\" of the element corresponding " +
    "to the locator \"%s:%s\" is \"%s\" which is same as expected";
  private static final String FAILURE_MESSAGE = "The CSS value for the property \"%s\" of the element corresponding " +
    "to the locator  <b>\"%s:%s\"</b> is \"%s\" ,which is not same as expected.<br>Expected:\"%s\" <br>Actual:\"%s\"";
  private static final String FAILURE_MESSAGE_NOT_AVAILABLE = "The element corresponding to locator  <b>\"%s:%s\"</b>" +
    "does not have CSS property <b>\"%s\"</b>";


  @Override
  public void execute() throws Exception {
    findElement();
    setActualValue(getElement().getCssValue(getAttribute()));
    Assert.notNull(getActualValue(), String.format(FAILURE_MESSAGE, getAttribute(), getFindByType(), getLocatorValue(), getActualValue(),
      getTestDataMaskResult(), getActualValue()));
    Assert.isTrue(!(getActualValue().toString().isEmpty()), String.format(FAILURE_MESSAGE_NOT_AVAILABLE, getFindByType(),
      getLocatorValue(), getAttribute()));
    Assert.isTrue(getTestData().equalsIgnoreCase(getActualValue().toString()),
      String.format(FAILURE_MESSAGE, getAttribute(), getFindByType(), getLocatorValue(), getActualValue(), getTestDataMaskResult(), getActualValue()));
    setSuccessMessage(String.format(SUCCESS_MESSAGE, getAttribute(), getFindByType(), getLocatorValue(), getActualValue()));
  }
}
