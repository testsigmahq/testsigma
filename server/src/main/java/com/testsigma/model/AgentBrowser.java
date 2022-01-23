/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import lombok.Data;

@Data
public class AgentBrowser {
  private OSBrowserType name;
  private String version;
  private int arch;
  private int majorVersion;
}
