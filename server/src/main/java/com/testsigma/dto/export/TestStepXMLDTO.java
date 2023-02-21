/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@Data
@JsonListRootName(name = "test-steps")
@JsonRootName(value = "test-step")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepXMLDTO extends BaseXMLDTO {
  @JsonProperty("name")
  private Long id;
  @JsonProperty("step-description")
  private String stepDescription;
  @JsonProperty("priority")
  private TestStepPriority priority;
  @JsonProperty("position")
  private Integer position;
  @JsonProperty("pre-requisite-step-id")
  private Long preRequisiteStepId;
  @JsonProperty("action")
  private String action;
  @JsonProperty("test-case-id")
  private Long testCaseId;
  @JsonProperty("addon-plugin-action-id")
  private Long addonActionId;
  @JsonProperty("test-group-id")
  private Long stepGroupId;
  @JsonProperty("custom-fields")
  private String customFields;
  @JsonProperty("excepted-result")
  private String exceptedResult;
  @JsonProperty("natural-text-action-id")
  private Integer naturalTextActionId;
  @JsonProperty("type")
  private TestStepType type;
  @JsonProperty("wait-time")
  private Integer waitTime;
  @JsonProperty("condition-type")
  private TestStepConditionType conditionType;
  @JsonProperty("parent-id")
  private Long parentId;
  @JsonProperty("record-active")
  private Integer recordActive = 1;
  @JsonIgnore
  private String testStepKey;
  @JsonProperty("IgnoreStepResult")
  private Boolean ignoreStepResult;
  @JsonIgnore
  private Long copiedFrom;
  @JsonProperty("phone-number-id")
  private String phoneNumberId;
  @JsonProperty("mail-box-id")
  private Long mailBoxId;
  @JsonProperty("created-by")
  private Long createdBy;
  @JsonProperty("updated-by")
  private Long updatedBy;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("created-date")
  private Timestamp createdDate;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("disabled")
  private Boolean disabled;
  @JsonProperty("VisualEnabled")
  private Boolean visualEnabled = false;
  @JsonProperty("condition_if")
  private ResultConstant[] ifConditionExpectedResults;
  @JsonProperty("dataMap")
  private TestStepDataMap dataMap;
  @JsonProperty("MaxIterations")
  private Integer maxIterations;
  @JsonProperty("test-data-function")
  private DefaultDataGenerator defaultDataGenerator;
  @JsonProperty("custom-step")
  private TestStepCustomStep customStep;
  @JsonProperty("element")
  private String element;
  @JsonProperty("from-element")
  private String fromElement;
  @JsonProperty("to-element")
  private String toElement;
  @JsonProperty("attribute")
  private String attribute;
  @JsonProperty("for-loop-start-index")
  private Integer forLoopStartIndex;
  @JsonProperty("for-loop-end-index")
  private Integer forLoopEndIndex;
  @JsonProperty("for-loop-test-data-id")
  private Long forLoopTestDataId;
  @JsonProperty("test-data-function-id")
  private Long testDataFunctionId;
  @JsonProperty("addon-test-data-function")
  private AddonTestStepTestData addonTDF;
  @JsonProperty("test-data-profile-step-id")
  private Long testDataProfileStepId;
  @JsonProperty("addon-test-data")
  private Map<String, AddonTestStepTestData> addonTestData;
  @JsonProperty("addon-elements")
  private Map<String, AddonElementData> addonElements = new HashMap<>();
  @JsonProperty("test_data_function_args")
  private Map<String, String> testDataFunctionArgs;
}
