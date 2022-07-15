/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@EqualsAndHashCode
public class DryTestPlanDTO {
  Long id;
  Long workspaceVersionId;
  String name;
  String description;
  Integer elementTimeOut;
  Integer pageTimeout;
  Long environmentId;
  Screenshot screenshot;
  RecoverAction recoveryAction;
  OnAbortedAction onAbortedAction;
  PreRequisiteAction onSuitePreRequisiteFail;
  PreRequisiteAction onTestcasePreRequisiteFail;
  RecoverAction onStepPreRequisiteFail;
  //TestPlanLabType testPlanLabType;
  TestPlanType testPlanType;
  WorkspaceVersionDTO workspaceVersion;
  List<TestDeviceDTO> testDevices;
}
