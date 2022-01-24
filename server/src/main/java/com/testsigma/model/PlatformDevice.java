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

@Data
public class PlatformDevice {
  private Long id;
  private Platform platform;
  private String osName;
  private String osVersion;
  private String name;
  private String displayName;
  private Boolean isAvailable = Boolean.FALSE;
}
