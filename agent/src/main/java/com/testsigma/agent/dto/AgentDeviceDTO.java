/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.dto;


import com.testsigma.agent.browsers.AgentBrowser;
import com.testsigma.agent.constants.MobileOs;
import lombok.Data;

import java.util.List;

@Data
public class AgentDeviceDTO {
  private String name;
  private String uniqueId;
  private String productModel;
  private String apiLevel;
  private String osVersion;
  private MobileOs osName;
  private String abi;
  private Boolean isEmulator;
  private Boolean isOnline;
  private Integer screenWidth;
  private Integer screenHeight;
  private List<AgentBrowser> browserList;
}
