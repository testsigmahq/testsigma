/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class TestPlanDTO {
  Long id;
  Long workspaceVersionId;
  String name;
  String description;
  Integer elementTimeOut;
  Integer pageTimeOut;
  Long environmentId;
  Screenshot screenshot;
  RecoverAction recoveryAction;
  OnAbortedAction onAbortedAction;
  PreRequisiteAction onSuitePreRequisiteFail;
  @JsonProperty("onTestCasePreRequisiteFail")
  PreRequisiteAction onTestcasePreRequisiteFail;
  RecoverAction onStepPreRequisiteFail;
  ReRunType reRunType;
  TestPlanLabType testPlanLabType;
  TestPlanType testPlanType;
  WorkspaceVersionDTO workspaceVersion;
  TestPlanResultDTO lastRun;
  Timestamp createdDate;
  Timestamp updatedDate;
  Boolean matchBrowserVersion = false;
  boolean visualTestingEnabled;
  Boolean retrySessionCreation;
  Integer retrySessionCreationTimeout;
  private Long lastRunId;
  private List<TestDeviceDTO> testDevices = new ArrayList<>();
  private String entityType;
}
