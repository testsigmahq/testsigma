/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.request;

import com.testsigma.automator.entity.WorkspaceType;
import com.testsigma.automator.entity.Platform;
import lombok.Data;

@Data
public class WebDriverSettingsRequest {
  private WorkspaceType workspaceType;
  private Platform platform;
  private String platformVersion;
  private String deviceName;
  private String browserName;
  private String browserVersion;
}
