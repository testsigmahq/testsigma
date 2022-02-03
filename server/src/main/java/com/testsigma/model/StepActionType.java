package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StepActionType {
  IF_CONDITION(0, "If"),
  WHILE_LOOP(1, "While");

  @Getter
  private Integer id;
  @Getter
  private String name;

  public static StepActionType getConditionType(Integer id) {
    switch (id) {
      case 0:
        return IF_CONDITION;
      case 1:
        return WHILE_LOOP;
      default:
        return null;
    }
  }
}

