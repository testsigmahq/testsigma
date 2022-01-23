/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.MobileOs;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AgentDeviceDTO {
  private Long id;
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
  private Boolean provisioned = false;
  private List<AgentBrowserDTO> browserList = new ArrayList<>();
}
