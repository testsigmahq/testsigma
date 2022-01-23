/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.drivers;

import lombok.Data;

@Data
public class WebDriverCapability {

  private String capabilityName;
  private Object capabilityValue;

  public WebDriverCapability() {

  }

  public WebDriverCapability(String capabilityName, Object capabilityValue) {
    this.capabilityName = capabilityName;
    this.capabilityValue = capabilityValue;
  }
}
