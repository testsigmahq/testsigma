/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class TestPlanRequest {
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
  TestPlanType testPlanType;
  Timestamp createdDate;
  Timestamp updatedDate;
  Boolean matchBrowserVersion = false;
  List<TestDeviceRequest> testDevices;
  ReRunType reRunType;
  Boolean retrySessionCreation = false;
  Integer retrySessionCreationTimeout;
}
