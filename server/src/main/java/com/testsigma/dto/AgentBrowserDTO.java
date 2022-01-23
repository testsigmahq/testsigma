/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.OSBrowserType;
import lombok.Data;

@Data
public class AgentBrowserDTO {
  private OSBrowserType name;
  private String version;
  private int arch;
  private int majorVersion;
}
