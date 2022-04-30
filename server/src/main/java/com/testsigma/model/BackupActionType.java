/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
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
public enum BackupActionType {
  NOT_USED(0, "Not Used"),
  EXPORT(1, "Export"),
  IMPORT(2, "Import");
  private Integer id;
  private String name;


  public static Map<Integer, String> getMap() {
    Map<Integer, String> toReturn = new HashMap<Integer, String>();
    for (BackupActionType type : BackupActionType.values()) {
      if (type != NOT_USED) {
        toReturn.put(type.getId(), type.getName());
      }
    }
    return toReturn;
  }

}
