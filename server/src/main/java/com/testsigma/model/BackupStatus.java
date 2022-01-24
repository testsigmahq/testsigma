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
public enum BackupStatus {
  NOT_USED(0, "Not Used"),
  IN_PROGRESS(1, "In progress"),
  SUCCESS(2, "Success"),
  FAILURE(3, "Failure");
  private Integer id;
  private String name;


  public static Map<Integer, String> getMap() {
    Map<Integer, String> toReturn = new HashMap<Integer, String>();
    for (BackupStatus type : BackupStatus.values()) {
      if (type != NOT_USED) {
        toReturn.put(type.getId(), type.getName());
      }
    }
    return toReturn;
  }

}
