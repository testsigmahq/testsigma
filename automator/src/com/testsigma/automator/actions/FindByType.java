/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.actions;

import com.testsigma.automator.entity.LocatorType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FindByType {
  ID,
  NAME,
  CLASS_NAME,
  CSS_SELECTOR,
  TAG_NAME,
  XPATH,
  ACCESSIBILITY_ID,
  LINK_TEXT,
  PARTIAL_LINK_TEXT;


  //TODO : Need to remove this post migratign old test steps to new test steps.
  // As of now we are mapping LocatorType enum to FindByType
  public static FindByType getType(LocatorType locatorType) {
    switch (locatorType) {
      case xpath:
        return FindByType.XPATH;
      case csspath:
        return FindByType.CSS_SELECTOR;
      case id_value:
        return FindByType.ID;
      case name:
        return FindByType.NAME;
      case link_text:
        return FindByType.LINK_TEXT;
      case partial_link_text:
        return FindByType.PARTIAL_LINK_TEXT;
      case class_name:
        return FindByType.CLASS_NAME;
      case tag_name:
        return FindByType.TAG_NAME;
      case accessibility_id:
        return FindByType.ACCESSIBILITY_ID;
    }
    return null;
  }
}
