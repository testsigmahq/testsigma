/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Setter;

@Data
public class StepDetails {
  private Long id;
  @JsonProperty("step_description")
  private String stepDescription;
  private TestStepPriority priority;
  @JsonProperty("order_id")
  private Integer position;
  @JsonProperty("prerequisite")
  private Long preRequisiteStepId;
  private String action;
  @JsonProperty("test_case_id")
  private Long testCaseId;
  @JsonProperty("step_group_id")
  private Long stepGroupId;
  @JsonProperty("data_map")
  private TestStepDataMap dataMap;
  @JsonProperty("natural_text_action_id")
  private Integer naturalTextActionId;
  @Setter
  private TestStepType type;
  @JsonProperty("wait_time")
  private Integer waitTime;
  @Setter
  @JsonProperty("condition_type")
  private TestStepConditionType conditionType;
  @JsonProperty("parent_id")
  private Long parentId;
  @JsonProperty("test_data_name")
  private String testDataName;
  @JsonProperty("test_data_value")
  private String testDataValue;
  @JsonProperty("ignore_step_result")
  private Boolean ignoreStepResult;
  @JsonProperty("max_iterations")
  private Integer maxIterations;

  public TestStepConditionType getConditionType() {
    return conditionType != null ? conditionType : null;
  }

  public TestStepPriority getPriority() {
    return priority != null ? priority : null;
  }

  public TestStepType getType() {
    return type != null ? type : null;
  }
}
