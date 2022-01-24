/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.mobileweb.swipe;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.swipe.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MobileWebSwipeElementProxyAction extends MobileNativeSwipeElementProxyAction {

  @Override
  public void execute() throws Exception {
    String direction = getTestData();
    switch (direction) {
      case ActionConstants.TOP_TO_BOTTOM:
        SwipeFromTopToBottomAction topToBottom = (SwipeFromTopToBottomAction) this.initializeChildSnippet(SwipeFromTopToBottomAction.class);
        topToBottom.execute();
        this.setSuccessMessage(topToBottom.getSuccessMessage());
        break;
      case ActionConstants.MIDDLE_TO_TOP:
        SwipeFromMiddleToTopAction middleToTop = (SwipeFromMiddleToTopAction) this.initializeChildSnippet(SwipeFromMiddleToTopAction.class);
        middleToTop.execute();
        this.setSuccessMessage(middleToTop.getSuccessMessage());
        break;
      case ActionConstants.BOTTOM_TO_TOP:
        SwipeFromBottomToTopAction bottomToTop = (SwipeFromBottomToTopAction) this.initializeChildSnippet(SwipeFromBottomToTopAction.class);
        bottomToTop.execute();
        this.setSuccessMessage(bottomToTop.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Swipe Action due to error at swipe direction");
        throw new AutomatorException("Unable to Perform Swipe Action due to error at swipe direction");
    }

  }
}
