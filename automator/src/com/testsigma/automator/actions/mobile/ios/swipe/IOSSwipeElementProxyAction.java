/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.ios.swipe;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.swipe.MobileNativeSwipeElementProxyAction;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class IOSSwipeElementProxyAction extends MobileNativeSwipeElementProxyAction {

  @Override
  public void execute() throws Exception {
    String direction = getTestData();
    switch (direction) {
      case ActionConstants.LEFT:
        SwipeFromElementToLeftAction left = (SwipeFromElementToLeftAction) this.initializeChildSnippet(SwipeFromElementToLeftAction.class);
        left.execute();
        this.setSuccessMessage(left.getSuccessMessage());
        break;

      case ActionConstants.RIGHT:
        SwipeFromElementToRightAction right = (SwipeFromElementToRightAction) this.initializeChildSnippet(SwipeFromElementToRightAction.class);
        right.execute();
        this.setSuccessMessage(right.getSuccessMessage());
        break;

      case ActionConstants.TOP:
        SwipeFromElementToTopAction top = (SwipeFromElementToTopAction) this.initializeChildSnippet(SwipeFromElementToTopAction.class);
        top.execute();
        this.setSuccessMessage(top.getSuccessMessage());
        break;

      case ActionConstants.BOTTOM:
        SwipeFromElementToBottomAction bottom = (SwipeFromElementToBottomAction) this.initializeChildSnippet(SwipeFromElementToBottomAction.class);
        bottom.execute();
        this.setSuccessMessage(bottom.getSuccessMessage());
        break;

      case ActionConstants.LEFT_TO_RIGHT:
        SwipeFromLeftToRightAction leftToRight = (SwipeFromLeftToRightAction) initializeChildSnippet(SwipeFromLeftToRightAction.class);
        leftToRight.execute();
        this.setSuccessMessage(leftToRight.getSuccessMessage());
        break;

      case ActionConstants.LEFT_TO_MIDDLE:
        SwipeFromLeftToMiddleAction leftToMiddle = (SwipeFromLeftToMiddleAction) this.initializeChildSnippet(SwipeFromLeftToMiddleAction.class);
        leftToMiddle.execute();
        this.setSuccessMessage(leftToMiddle.getSuccessMessage());
        break;

      case ActionConstants.MIDDLE_TO_LEFT:
        SwipeFromMiddleToLeftAction middleToLeft = (SwipeFromMiddleToLeftAction) this.initializeChildSnippet(SwipeFromMiddleToLeftAction.class);
        middleToLeft.execute();
        this.setSuccessMessage(middleToLeft.getSuccessMessage());
        break;

      case ActionConstants.RIGHT_TO_LEFT:
        SwipeFromRightToLeftAction rightToLeft = (SwipeFromRightToLeftAction) this.initializeChildSnippet(SwipeFromRightToLeftAction.class);
        rightToLeft.execute();
        this.setSuccessMessage(rightToLeft.getSuccessMessage());
        break;

      case ActionConstants.MIDDLE_TO_RIGHT:
        SwipeFromMiddleToRightAction middleToRight = (SwipeFromMiddleToRightAction) this.initializeChildSnippet(SwipeFromMiddleToRightAction.class);
        middleToRight.execute();
        this.setSuccessMessage(middleToRight.getSuccessMessage());
        break;

      case ActionConstants.RIGHT_TO_MIDDLE:
        SwipeFromRightToMiddleAction rightToMiddle = (SwipeFromRightToMiddleAction) this.initializeChildSnippet(SwipeFromRightToMiddleAction.class);
        rightToMiddle.execute();
        this.setSuccessMessage(rightToMiddle.getSuccessMessage());
        break;

      case ActionConstants.TOP_TO_BOTTOM:
        SwipeFromTopToBottomAction topToBottom = (SwipeFromTopToBottomAction) this.initializeChildSnippet(SwipeFromTopToBottomAction.class);
        topToBottom.execute();
        this.setSuccessMessage(topToBottom.getSuccessMessage());
        break;
      case ActionConstants.TOP_TO_MIDDLE:
        SwipeFromTopToMiddleAction topToMiddle = (SwipeFromTopToMiddleAction) this.initializeChildSnippet(SwipeFromTopToMiddleAction.class);
        topToMiddle.execute();
        this.setSuccessMessage(topToMiddle.getSuccessMessage());
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
      case ActionConstants.BOTTOM_TO_MIDDLE:
        SwipeFromBottomToMiddleAction bottomToMiddle = (SwipeFromBottomToMiddleAction) this.initializeChildSnippet(SwipeFromBottomToMiddleAction.class);
        bottomToMiddle.execute();
        this.setSuccessMessage(bottomToMiddle.getSuccessMessage());
        break;
      case ActionConstants.MIDDLE_TO_BOTTOM:
        SwipeFromMiddleToBottomAction middleToBottom = (SwipeFromMiddleToBottomAction) this.initializeChildSnippet(SwipeFromMiddleToBottomAction.class);
        middleToBottom.execute();
        this.setSuccessMessage(middleToBottom.getSuccessMessage());
      default:
        setErrorMessage("Unable to Perform Swipe Action due to error at swipe direction");
        throw new AutomatorException("Unable to Perform Swipe Action due to error at swipe direction");
    }

  }

}
