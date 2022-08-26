/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum TestStepConditionType {
  NOT_USED(0,"Not Used"),
  CONDITION_IF(1,"If"),
  CONDITION_IFNOT(2,"If not"),
  CONDITION_ELSE_IF(3,"Else If"),
  CONDITION_ELSE_IFNOT(4,"Else If not"),
  CONDITION_ELSE(5,"Else"),
  LOOP_FOR(6,"For"),
  LOOP_WHILE(7,"While");

  private final Integer id;
  private final String name;


  public static TestStepConditionType getConditionType(Integer id) {
    switch (id) {
      case 1:
        return CONDITION_IF;
      case 2:
        return CONDITION_IFNOT;
      case 3:
        return CONDITION_ELSE_IF;
      case 4:
        return CONDITION_ELSE_IFNOT;
      case 5:
        return CONDITION_ELSE;
      case 6:
        return LOOP_FOR;
      case 7:
        return LOOP_WHILE;

      default:
        return null;

    }
  }
}
