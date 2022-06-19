/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.*;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;

import java.sql.Timestamp;


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
  @JsonProperty("test-data")
  private String testData;
  @JsonProperty("test-data-function")
  private DefaultDataGenerator defaultDataGenerator;
  @JsonProperty("custom-step")
  private TestStepCustomStep customStep;
  @JsonProperty("test-data-type")
  private String testDataType;
  @JsonProperty("element")
  private String element;
  @JsonProperty("from-element")
  private String fromElement;
  @JsonProperty("to-element")
  private String toElement;
  @JsonProperty("attribute")
  private String attribute;
  @JsonProperty("for_loop")
  private TestStepForLoop forLoop;
  @JsonProperty("while_loop")
  private TestStepWhileLoop whileLoop;
  @JsonProperty("whileCondition")
  private String whileCondition;
  @JsonProperty("addon_test_data_function")
  private AddonTestStepTestData addonTDF;

}
