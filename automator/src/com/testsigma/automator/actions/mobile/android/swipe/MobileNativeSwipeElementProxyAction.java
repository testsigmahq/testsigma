/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.android.swipe;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class MobileNativeSwipeElementProxyAction extends com.testsigma.automator.actions.mobile.swipe.MobileNativeSwipeElementProxyAction {

  @Override
  public void execute() throws Exception {
    String direction = getTestData();
    switch (direction) {
      case ActionConstants.LEFT:
        MobileNativeSwipeElementToLeftSnippet left = (MobileNativeSwipeElementToLeftSnippet) this.initializeChildSnippet(MobileNativeSwipeElementToLeftSnippet.class);
        left.execute();
        this.setSuccessMessage(left.getSuccessMessage());
        break;

      case ActionConstants.RIGHT:
        MobileNativeSwipeElementToRightSnippet right = (MobileNativeSwipeElementToRightSnippet) this.initializeChildSnippet(MobileNativeSwipeElementToRightSnippet.class);
        right.execute();
        this.setSuccessMessage(right.getSuccessMessage());
        break;

      case ActionConstants.TOP:
        MobileNativeSwipeElementToTopSnippet top = (MobileNativeSwipeElementToTopSnippet) this.initializeChildSnippet(MobileNativeSwipeElementToTopSnippet.class);
        top.execute();
        this.setSuccessMessage(top.getSuccessMessage());
        break;

      case ActionConstants.BOTTOM:
        MobileNativeSwipeElementToBottomSnippet bottom = (MobileNativeSwipeElementToBottomSnippet) this.initializeChildSnippet(MobileNativeSwipeElementToBottomSnippet.class);
        bottom.execute();
        this.setSuccessMessage(bottom.getSuccessMessage());
        break;

      case ActionConstants.LEFT_TO_RIGHT:
        MobileNativeSwipeLeftToRightAction leftToRight = (MobileNativeSwipeLeftToRightAction) this.initializeChildSnippet(MobileNativeSwipeLeftToRightAction.class);
        leftToRight.execute();
        this.setSuccessMessage(leftToRight.getSuccessMessage());
        break;

      case ActionConstants.LEFT_TO_MIDDLE:
        MobileNativeSwipeLeftToMiddleAction leftToMiddle = (MobileNativeSwipeLeftToMiddleAction) this.initializeChildSnippet(MobileNativeSwipeLeftToMiddleAction.class);
        leftToMiddle.execute();
        this.setSuccessMessage(leftToMiddle.getSuccessMessage());
        break;

      case ActionConstants.MIDDLE_TO_LEFT:
        MobileNativeSwipeMiddleToLeftAction middleToLeft = (MobileNativeSwipeMiddleToLeftAction) this.initializeChildSnippet(MobileNativeSwipeMiddleToLeftAction.class);
        middleToLeft.execute();
        this.setSuccessMessage(middleToLeft.getSuccessMessage());
        break;

      case ActionConstants.RIGHT_TO_LEFT:
        MobileNativeSwipeRightToLeftAction rightToLeft = (MobileNativeSwipeRightToLeftAction) this.initializeChildSnippet(MobileNativeSwipeRightToLeftAction.class);
        rightToLeft.execute();
        this.setSuccessMessage(rightToLeft.getSuccessMessage());
        break;

      case ActionConstants.MIDDLE_TO_RIGHT:
        MobileNativeSwipeMiddleToRightAction middleToRight = (MobileNativeSwipeMiddleToRightAction) this.initializeChildSnippet(MobileNativeSwipeMiddleToRightAction.class);
        middleToRight.execute();
        this.setSuccessMessage(middleToRight.getSuccessMessage());
        break;

      case ActionConstants.RIGHT_TO_MIDDLE:
        MobileNativeSwipeRightToMiddleAction rightToMiddle = (MobileNativeSwipeRightToMiddleAction) this.initializeChildSnippet(MobileNativeSwipeRightToMiddleAction.class);
        rightToMiddle.execute();
        this.setSuccessMessage(rightToMiddle.getSuccessMessage());
        break;

      case ActionConstants.TOP_TO_BOTTOM:
        MobileNativeSwipeTopToBottomAction topToBottom = (MobileNativeSwipeTopToBottomAction) this.initializeChildSnippet(MobileNativeSwipeTopToBottomAction.class);
        topToBottom.execute();
        this.setSuccessMessage(topToBottom.getSuccessMessage());
        break;
      case ActionConstants.TOP_TO_MIDDLE:
        MobileNativeSwipeTopToMiddleAction topToMiddle = (MobileNativeSwipeTopToMiddleAction) this.initializeChildSnippet(MobileNativeSwipeTopToMiddleAction.class);
        topToMiddle.execute();
        this.setSuccessMessage(topToMiddle.getSuccessMessage());
        break;
      case ActionConstants.MIDDLE_TO_TOP:
        MobileNativeSwipeMiddleToTopAction middleToTop = (MobileNativeSwipeMiddleToTopAction) this.initializeChildSnippet(MobileNativeSwipeMiddleToTopAction.class);
        middleToTop.execute();
        this.setSuccessMessage(middleToTop.getSuccessMessage());
        break;
      case ActionConstants.BOTTOM_TO_TOP:
        MobileNativeSwipeBottomToTopAction bottomToTop = (MobileNativeSwipeBottomToTopAction) this.initializeChildSnippet(MobileNativeSwipeBottomToTopAction.class);
        bottomToTop.execute();
        this.setSuccessMessage(bottomToTop.getSuccessMessage());
        break;
      case ActionConstants.BOTTOM_TO_MIDDLE:
        MobileNativeSwipeBottomToMiddleAction bottomToMiddle = (MobileNativeSwipeBottomToMiddleAction) this.initializeChildSnippet(MobileNativeSwipeBottomToMiddleAction.class);
        bottomToMiddle.execute();
        this.setSuccessMessage(bottomToMiddle.getSuccessMessage());
        break;
      case ActionConstants.MIDDLE_TO_BOTTOM:
        MobileNativeSwipeMiddleToBottomAction middleToBottom = (MobileNativeSwipeMiddleToBottomAction) this.initializeChildSnippet(MobileNativeSwipeMiddleToBottomAction.class);
        middleToBottom.execute();
        this.setSuccessMessage(middleToBottom.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Swipe Action due to error at swipe direction");
        throw new AutomatorException("Unable to Perform Swipe Action due to error at swipe direction");
    }

  }

}
