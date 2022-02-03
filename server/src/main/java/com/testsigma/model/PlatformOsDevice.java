/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import lombok.Data;

import java.util.List;

@Data
public class PlatformOsDevice {
  private String osName;
  private String osDisplayName;
  private List<PlatformDevice> platformDevices;
}
