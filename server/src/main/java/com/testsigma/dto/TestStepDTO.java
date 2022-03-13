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
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TestStepDTO implements Cloneable, Serializable {

  private Long id;
  private TestStepPriority priority;
  private Integer position;
  private Long preRequisiteStepId;
  private String action;
  private Long testCaseId;
  private Long stepGroupId;

  @JsonProperty("conditionIf")
  private ResultConstant[] ifConditionExpectedResults = new ResultConstant[0];
  private String testData;
  private String testDataType;
  private String attribute;
  private String element;
  private String fromElement;
  private String toElement;
  private Integer forLoopStartIndex;
  private Integer forLoopEndIndex;
  private Long forLoopTestDataId;
  private Long testDataFunctionId;
  @JsonProperty()
  private AddonTestStepTestData addonTDF;
  private Map<String, String> testDataFunctionArgs;
  private String exceptedResult;
  private Integer naturalTextActionId;
  private TestStepType type;
  private Integer waitTime;
  private TestStepConditionType conditionType;
  private Long parentId;
  private RestStepDTO restStep;
  private Long phoneNumberId;
  private Long addonActionId;
  //  private AddonNaturalTextActionData addonNaturalTextActionData;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  private Boolean disabled;

  private List<TestStepDTO> testStepDTOS = new ArrayList<>();

  public TestStepDTO clone() throws CloneNotSupportedException {
    TestStepDTO entity = (TestStepDTO) super.clone();
    List<TestStepDTO> steps = new ArrayList<>();
    for (TestStepDTO step : testStepDTOS) {
      steps.add(step.clone());
    }
    entity.setTestStepDTOS(steps);
    return entity;
  }

  // We are using dataMap as JSON in TestcaseStepEntity
  public Map<String, Object> getDataMapJson() {
    JSONObject json = new JSONObject();
    json.put("conditionIf", ifConditionExpectedResults);
    json.put("testData", testData);
    json.put("testDataType", testDataType);
    json.put("element", element);
    json.put("fromElement", fromElement);
    json.put("toElement", toElement);
    json.put("attribute", attribute);
    json.put("testDataFunctionId", testDataFunctionId);
    json.put("testDataFunctionArgs", testDataFunctionArgs);
    json.put("forLoopStartIndex", forLoopStartIndex);
    json.put("forLoopEndIndex", forLoopEndIndex);
    json.put("forLoopTestDataId", forLoopTestDataId);
    return json.toMap();
  }

  // we are using TestStepDataMap bean object in TestStepResult: stepDetails
  public TestStepDataMap getDataMapBean() {
    TestStepDataMap testStepDataMap = new TestStepDataMap();
    testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
    testStepDataMap.setTestData(testData);
    testStepDataMap.setTestDataType(testDataType);
    testStepDataMap.setElement(element);
    testStepDataMap.setFromElement(fromElement);
    testStepDataMap.setToElement(toElement);
    testStepDataMap.setAttribute(attribute);
    testStepDataMap.setAddonTDF(addonTDF);
    DefaultDataGeneratorsDetails functionDetails = new DefaultDataGeneratorsDetails();
    functionDetails.setId(testDataFunctionId);
    functionDetails.setArguments(testDataFunctionArgs);
    TestStepForLoop forLoop = new TestStepForLoop();
    forLoop.setStartIndex(forLoopStartIndex);
    forLoop.setEndIndex(forLoopEndIndex);
    forLoop.setTestDataId(forLoopTestDataId);
    testStepDataMap.setForLoop(forLoop);
    return testStepDataMap;
  }
}
