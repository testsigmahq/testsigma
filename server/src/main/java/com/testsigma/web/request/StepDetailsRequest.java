package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.TestStepConditionType;
import com.testsigma.model.TestStepPriority;
import com.testsigma.model.TestStepType;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StepDetailsRequest {
  private Long id;
  private String stepDescription;
  private TestStepPriority priority;
  private Integer position;
  private Long preRequisiteStepId;
  private String action;
  private Long testCaseId;
  private Long stepGroupId;
  private TestStepDataMapRequest dataMap;
  private String exceptedResult;
  @JsonProperty("natural_text_action_id")
  private Integer naturalTextActionId;
  private TestStepType type;
  private Integer waitTime;
  private TestStepConditionType conditionType;
  private Long parentId;
  private String testDataName;
  private String testDataValue;
}
