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
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepPriority;
import com.testsigma.model.TestStepType;
import lombok.Data;


@Data
public class StepDetailsDTO {
  private Long id;
  private String stepDescription;
  private TestStepPriority priority;
  @JsonProperty("order_id")
  private Integer position;
  private Long preRequisiteStepId;
  private String action;
  private Long testCaseId;
  private Long stepGroupId;
  private TestStepDataMapEntityDTO dataMap;
  private String addonNaturalTextActionData;
  private Long addonActionId;
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
  private Long maxIterations;
}
