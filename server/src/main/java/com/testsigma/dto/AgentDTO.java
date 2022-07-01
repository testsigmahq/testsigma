/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.AgentOs;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class AgentDTO {
  private Long id;
  private String uniqueId;
  @ToString.Exclude
  private String jwtApiKey;
  private String agentVersion;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private List<AgentBrowserDTO> browserList = new ArrayList<>();
  private String title;
  private String ipAddress;
  private String hostName;
  private AgentOs osType;
  private String osVersion;
  private String currentAgentVersion = "1.6.3";
}
