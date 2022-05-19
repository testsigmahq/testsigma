/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.OSBrowserType;
import com.testsigma.model.Platform;
import lombok.Data;

@Data
public class PrivateGridBrowserDTO {
  private OSBrowserType browserName;
  private Integer maxInstances;
  private Platform platform;
  private String platformName;
  private String version;
}
