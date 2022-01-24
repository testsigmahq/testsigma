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

@AllArgsConstructor
@Getter
public enum FilterOperatorType {
  Contains(1, "contains"),
  Doesnt_contain(2, "doesn't contain");
  private final Integer id;
  private final String name;


  public static Map<Integer, String> getMap() {
    Map<Integer, String> toReturn = new HashMap<Integer, String>();
    for (FilterOperatorType type : FilterOperatorType.values()) {
      toReturn.put(type.getId(), type.getName());
    }
    return toReturn;
  }

}
