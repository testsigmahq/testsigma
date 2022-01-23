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
public enum ExpectedResult {
  NOT_USED(0, "Not Used"),
  STATUS(1, "status"),
  HEADER_CONTENT(2, "header content"),
  BODY_CONTENT(3, "body content"),
  ALL(4, "all");

  private final Integer id;
  private final String name;


  public static Map<Integer, String> getMap() {
    Map<Integer, String> toReturn = new HashMap<Integer, String>();
    for (ExpectedResult type : ExpectedResult.values()) {
      if (type != NOT_USED) {
        toReturn.put(type.getId(), type.getName());
      }
    }
    return toReturn;
  }


}
