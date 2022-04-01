/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.automator.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.automator.constants.TestStepConditionType;
import lombok.Data;

@Data
public class StepDetails {
  private Long id;
  private String stepDescription;
  private TestStepPriority priority;
  private Integer position;
  private Long preRequisiteStepId;
  private String action;
  private Long testCaseId;
  private Long stepGroupId;
  private TestStepDataMapEntity dataMap;
  private String exceptedResult;
  @JsonProperty("natural_text_action_id")
  private Integer naturalTextActionId;
  private TestStepType type;
  private Integer waitTime;
  private TestStepConditionType conditionType;
  private Long parentId;
  private String testDataName;
  private String testDataValue;
  private Boolean ignoreStepResult;
}
