/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.automator.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TestStepType {

  ACTION_TEXT, STEP_GROUP, REST_STEP, FOR_LOOP, WHILE_LOOP, BREAK_LOOP, CONTINUE_LOOP;


  public static TestStepType getType(Integer id) {
    switch (id) {
      case 1:
        return ACTION_TEXT;
      case 2:
        return STEP_GROUP;
      case 3:
        return REST_STEP;
      case 4:
        return FOR_LOOP;
      case 5:
        return WHILE_LOOP;
      case 6:
        return BREAK_LOOP;
      case 7:
        return CONTINUE_LOOP;
    }
    return null;
  }

}
