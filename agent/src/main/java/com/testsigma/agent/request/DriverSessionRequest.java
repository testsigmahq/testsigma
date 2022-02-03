/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.request;

import com.testsigma.automator.entity.AppPathType;
import com.testsigma.automator.entity.WorkspaceType;
import com.testsigma.automator.entity.ExecutionLabType;
import com.testsigma.automator.entity.Platform;
import lombok.Data;

import java.net.URL;

@Data
public class DriverSessionRequest {
  private Long mobileSessionId;
  private ExecutionLabType executionLabType;
  private WorkspaceType workspaceType;
  private Platform platform;
  private String uniqueId;
  private URL webDriverServerUrl;
  private String jwtApiKey;
  private String agentUUID;
  private AppPathType applicationPathType;
}
