/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.automator.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.testsigma.automator.constants.StorageType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TestDeviceEntity {
  TestPlanRunSettingEntity testPlanSettings;
  private Long id;
  private Long environmentResultId;
  private String name;
  private Long executionId;
  private Long executionRunId;
  private WorkspaceType workspaceType;
  @JsonIgnore
  private boolean isTestcasesEmpty;
  private ExecutionLabType executionLabType;
  private String videoPreSignedURL;
  private String seleniumLogPreSignedURL;
  private String consoleLogPreSignedURL;
  private String harLogPreSignedURL;
  private String appiumLogPreSignedURL;
  private TestDeviceSettings envSettings;
  private List<TestSuiteEntity> testSuites = new ArrayList<TestSuiteEntity>();
  private Integer errorCode;
  private String agentDeviceUuid;
  private Boolean matchBrowserVersion;
  private Boolean runInParallel = false;
  private Boolean createSessionAtCaseLevel = false;
  @JsonIgnore
  private String username;
  @JsonIgnore
  private String accessKey;
  private StorageType storageType;
}
