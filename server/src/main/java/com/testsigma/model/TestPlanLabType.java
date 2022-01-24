/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TestPlanLabType {
  TestsigmaLab, Hybrid;

  public boolean isHybrid() {
    return this.equals(Hybrid);
  }
}
