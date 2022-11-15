/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ResultConstant {
  SUCCESS(0, "Passed", "#4bdb9b"),
  FAILURE(1, "Failed", "#4bdb9b"),
  ABORTED(2, "Aborted", "#4bdb9b"),
  NOT_EXECUTED(3, "Not Executed", "#4bdb9b"),
  QUEUED(4, "Queued", "#4bdb9b"),
  STOPPED(5, "Stopped", "#4bdb9b");

  private static final Map<Integer, ResultConstant> map = new HashMap<>();

  static {
    for (ResultConstant resultConstant : ResultConstant.values()) {
      map.put(resultConstant.id, resultConstant);
    }
  }

  private final Integer id;
  private final String name;
  private final String color;

  public static ResultConstant getResult(Integer id) {
    return map.get(id);
  }

  public static String getXrayStatus(ResultConstant resultConstant){
    switch (resultConstant){
      case SUCCESS:
        return "PASSED";
      case QUEUED:
        return "EXECUTING";
      default:
        return "FAILED";
    }
  }
}
