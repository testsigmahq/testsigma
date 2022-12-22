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
public enum TestDataType {

  //@|Parameter|, $|Runtime|, *|Environment !|Data Generator|, ~|Random|
  raw, parameter, runtime, global, environment, random,
  function, phone_number, mail_box;

  public static TestDataType getTestDataType(String name) {
    switch (name) {
      case "raw":
        return raw;
      case "parameter":
        return parameter;
      case "runtime":
        return runtime;
      case "random":
        return random;
      case "function":
        return function;
      case "global":
        return global;
    }
    return raw;
  }
}
