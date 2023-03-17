/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.agent.dto;

import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.constants.AgentOs;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Data
public class AgentDTO {
  private Long id;
  private String uniqueId;
  @ToString.Exclude
  private String jwtApiKey;
  private String agentVersion;
  private String hostName;
  private String osVersion;
  private Boolean isRegistered;
  private Set<AgentBrowser> browserList;
  private String agentBuild;
  private String ipAddress;
  private String title;
  private AgentOs osType;
}
