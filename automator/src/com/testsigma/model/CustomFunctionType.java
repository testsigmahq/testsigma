/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomFunctionType {
  DontUse(0, "Don't use"),
  DefaultTestData(1, "Default TestData Function"),
  DefaultTestStep(2, "Default TestStep Function"),
  CustomTestStep(3, "Custom Function"),
  CustomTestData(4, "Custom TestData Function");

  private Integer id;
  private String displayName;

  public static CustomFunctionType getFunctionType(Integer id) {
    switch (id) {
      case 0:
        return DontUse;
      case 1:
        return DefaultTestData;
      case 2:
        return DefaultTestStep;
      case 3:
        return CustomTestStep;
      case 4:
        return CustomTestData;
    }
    return DontUse;
  }
}
