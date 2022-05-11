/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum TestStepConditionType {
  NOT_USED("Not Used"),
  CONDITION_IF("If"),
  CONDITION_IFNOT("If not"),
  CONDITION_ELSE_IF("Else If"),
  CONDITION_ELSE_IFNOT("Else If not"),
  CONDITION_ELSE("Else"),
  LOOP_FOR("For"),
  LOOP_WHILE("While");

  @Getter
  private final String name;
}
