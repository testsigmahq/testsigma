/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.*;
import lombok.Data;

import java.util.Map;

@Data
public class TestStepRequest {
  private Long id;
  private TestStepPriority priority;
  private Integer position;
  private Long preRequisiteStepId;
  private String action;
  private Long testCaseId;
  private Long stepGroupId;
  private TestStepDataMap dataMap;
  @JsonProperty("conditionIf")
  private ResultConstant[] ifConditionExpectedResults;
  private String attribute;
  private String element;
  private String fromElement;
  private String toElement;
  private Long testDataFunctionId;
  private Map<String, String> testDataFunctionArgs;
  private Long maxIterations;
  private String exceptedResult;
  private Integer naturalTextActionId;
  private TestStepType type;
  private Integer waitTime;
  private TestStepConditionType conditionType;
  private Long parentId;
  private Long copiedFrom;
  private RestStepDetailsRequest restStep;
  private Long testDataId;
  private Long phoneNumberId;
  private Long addonActionId;
  private AddonNaturalTextActionData addonNaturalTextActionData;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  private Boolean disabled;
  private Boolean ignoreStepResult;
  private Boolean visualEnabled = false;
  private Long testDataProfileStepId;
  private ForLoopConditionRequest forLoopConditionsRequest;
  @JsonProperty()
  private AddonTestStepTestData addonTDF;
}
