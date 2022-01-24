/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.OSBrowserType;
import lombok.Data;

@Data
public class AgentBrowserRequest {
  private OSBrowserType name;
  private String version;
  private int arch;
  private int majorVersion;
}
