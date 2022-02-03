/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.OSBrowserType;
import lombok.Data;

@Data
public class AgentBrowserXMLDTO {
  private OSBrowserType name;
  private String version;
  private int arch;
  @JsonProperty("major-version")
  private int majorVersion;
}
