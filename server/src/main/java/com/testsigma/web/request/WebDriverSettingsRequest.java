/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.testsigma.model.AppPathType;
import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.WorkspaceType;
import com.testsigma.model.Platform;
import lombok.Data;

import java.net.URL;

@Data
public class WebDriverSettingsRequest {
  private Long mobileSessionId;
  private TestPlanLabType executionLabType;
  private WorkspaceType workspaceType;
  private Platform platform;
  private String platformVersion;
  private String deviceName;
  private String browserName;
  private String browserVersion;
  private URL webDriverServerUrl;
  private AppPathType applicationPathType;
  private Long applicationUploadedId;
  private String applicationPath;
  private String applicationPackage;
  private String applicationActivity;
  private String uniqueId;
  private Long agentDeviceId;
  private String bundleId;
}
