/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions.mobile.swipe;

import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.actions.constants.ActionConstants;
import com.testsigma.automator.actions.mobile.MobileElementAction;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;

@Log4j2
public class MobileNativeSwipeElementProxyAction extends MobileElementAction {

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
        MobileNativeSwipeLeftToRightSnippet leftToRight = (MobileNativeSwipeLeftToRightSnippet) this.initializeChildSnippet(MobileNativeSwipeLeftToRightSnippet.class);
        leftToRight.execute();
        this.setSuccessMessage(leftToRight.getSuccessMessage());
        break;

      case ActionConstants.LEFT_TO_MIDDLE:
        MobileNativeSwipeLeftToMiddleSnippet leftToMiddle = (MobileNativeSwipeLeftToMiddleSnippet) this.initializeChildSnippet(MobileNativeSwipeLeftToMiddleSnippet.class);
        leftToMiddle.execute();
        this.setSuccessMessage(leftToMiddle.getSuccessMessage());
        break;

      case ActionConstants.MIDDLE_TO_LEFT:
        MobileNativeSwipeMiddleToLeftSnippet middleToLeft = (MobileNativeSwipeMiddleToLeftSnippet) this.initializeChildSnippet(MobileNativeSwipeMiddleToLeftSnippet.class);
        middleToLeft.execute();
        this.setSuccessMessage(middleToLeft.getSuccessMessage());
        break;

      case ActionConstants.RIGHT_TO_LEFT:
        MobileNativeSwipeRightToLeftSnippet rightToLeft = (MobileNativeSwipeRightToLeftSnippet) this.initializeChildSnippet(MobileNativeSwipeRightToLeftSnippet.class);
        rightToLeft.execute();
        this.setSuccessMessage(rightToLeft.getSuccessMessage());
        break;

      case ActionConstants.MIDDLE_TO_RIGHT:
        MobileNativeSwipeMiddleToRightSnippet middleToRight = (MobileNativeSwipeMiddleToRightSnippet) this.initializeChildSnippet(MobileNativeSwipeMiddleToRightSnippet.class);
        middleToRight.execute();
        this.setSuccessMessage(middleToRight.getSuccessMessage());
        break;

      case ActionConstants.RIGHT_TO_MIDDLE:
        MobileNativeSwipeRightToMiddleSnippet rightToMiddle = (MobileNativeSwipeRightToMiddleSnippet) this.initializeChildSnippet(MobileNativeSwipeRightToMiddleSnippet.class);
        rightToMiddle.execute();
        this.setSuccessMessage(rightToMiddle.getSuccessMessage());
        break;

      case ActionConstants.TOP_TO_BOTTOM:
        MobileNativeSwipeTopToBottomSnippet topToBottom = (MobileNativeSwipeTopToBottomSnippet) this.initializeChildSnippet(MobileNativeSwipeTopToBottomSnippet.class);
        topToBottom.execute();
        this.setSuccessMessage(topToBottom.getSuccessMessage());
        break;
      case ActionConstants.TOP_TO_MIDDLE:
        MobileNativeSwipeTopToMiddleSnippet topToMiddle = (MobileNativeSwipeTopToMiddleSnippet) this.initializeChildSnippet(MobileNativeSwipeTopToMiddleSnippet.class);
        topToMiddle.execute();
        this.setSuccessMessage(topToMiddle.getSuccessMessage());
        break;
      case ActionConstants.MIDDLE_TO_TOP:
        MobileNativeSwipeMiddleToTopSnippet middleToTop = (MobileNativeSwipeMiddleToTopSnippet) this.initializeChildSnippet(MobileNativeSwipeMiddleToTopSnippet.class);
        middleToTop.execute();
        this.setSuccessMessage(middleToTop.getSuccessMessage());
        break;
      case ActionConstants.BOTTOM_TO_TOP:
        MobileNativeSwipeBottomToTopSnippet bottomToTop = (MobileNativeSwipeBottomToTopSnippet) this.initializeChildSnippet(MobileNativeSwipeBottomToTopSnippet.class);
        bottomToTop.execute();
        this.setSuccessMessage(bottomToTop.getSuccessMessage());
        break;
      case ActionConstants.BOTTOM_TO_MIDDLE:
        MobileNativeSwipeBottomToMiddleSnippet bottomToMiddle = (MobileNativeSwipeBottomToMiddleSnippet) this.initializeChildSnippet(MobileNativeSwipeBottomToMiddleSnippet.class);
        bottomToMiddle.execute();
        this.setSuccessMessage(bottomToMiddle.getSuccessMessage());
        break;
      case ActionConstants.MIDDLE_TO_BOTTOM:
        MobileNativeSwipeMiddleToBottomSnippet middleToBottom = (MobileNativeSwipeMiddleToBottomSnippet) this.initializeChildSnippet(MobileNativeSwipeMiddleToBottomSnippet.class);
        middleToBottom.execute();
        this.setSuccessMessage(middleToBottom.getSuccessMessage());
        break;
      default:
        setErrorMessage("Unable to Perform Swipe Action due to error at swipe direction");
        throw new AutomatorException("Unable to Perform Swipe Action due to error at swipe direction");
    }

  }

  protected Object initializeChildSnippet(Class<?> snippetClassName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    MobileElementAction snippet = (MobileElementAction) snippetClassName.getDeclaredConstructor().newInstance();
    snippet.setDriver(this.getDriver());
    snippet.setElement(this.getElement());
    snippet.setElementPropertiesEntityMap(this.getElementPropertiesEntityMap());
    snippet.setTestDataPropertiesEntityMap(this.getTestDataPropertiesEntityMap());
    snippet.setAttributesMap(this.getAttributesMap());
    snippet.setRuntimeDataProvider(this.getRuntimeDataProvider());
    snippet.setEnvSettings(this.getEnvSettings());
    return  snippet;
  }
}
