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
@Getter
public enum TestStepType {
  NOT_USED(0, "Not Used"),
  ACTION_TEXT(1, "Action Text"),
  STEP_GROUP(2, "Step Group"),
  REST_STEP(3, "Rest Step"),
  FOR_LOOP(4, "For Loop"),
  WHILE_LOOP(5, "While Loop"),
  BREAK_LOOP(6, "Break Loop"),
  CONTINUE_LOOP(7, "Continue Loop"),
  NLP_TEXT(8, "NLP Text"),
  CUSTOM_FUNCTION(9, "Custom Funtion");

  private final Integer id;

  private final String name;

  public static TestStepType getById(Integer id) {
    for (TestStepType type : values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    return null;
  }

}
