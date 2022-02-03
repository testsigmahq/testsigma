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
public enum RestResultCompareType {

  STATUS, HEADER, BODY, ALL;

  public static RestResultCompareType getCompareType(Integer id) {

    switch (id) {

      case 1:
        return STATUS;
      case 2:
        return HEADER;
      case 3:
        return BODY;
      case 4:
        return ALL;
    }
    return null;
  }

}
