/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.StorageType;
import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class EnvironmentEntityDTO implements Cloneable {
  private Long id;
  private Long environmentResultId;
  private String name;
  private Long executionId;
  private Long executionRunId;
  private WorkspaceType workspaceType;
  private TestPlanLabType executionLabType;
  private TestPlanSettingEntityDTO testPlanSettings;
  private TestDeviceSettingsDTO envSettings;
  private List<TestSuiteEntityDTO> testSuites;
  private List<TestCaseEntityDTO> testCases;
  private Integer errorCode;
  private String agentDeviceUuid;
  private Boolean matchBrowserVersion;
  private Boolean runInParallel = false;
  private Boolean createSessionAtCaseLevel = false;
  private StorageType storageType;

  @Override
  public EnvironmentEntityDTO clone() throws CloneNotSupportedException {
    return (EnvironmentEntityDTO) super.clone();
  }
}
