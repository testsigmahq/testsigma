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
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.model.*;
import com.testsigma.model.recorder.RecorderTestStepNlpData;
import com.testsigma.model.recorder.TestStepRecorderDataMap;
import com.testsigma.model.recorder.TestStepRecorderForLoop;
import lombok.Data;
import lombok.ToString;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
  private TestStepDataMap dataMap;
  private String attribute;
  private String element;
  private String fromElement;
  private String toElement;
  private Boolean visualEnabled = false;
  private Long testDataFunctionId;
  private String testDataProfileName;
  private Boolean processedAsSubStep = Boolean.FALSE;
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
  private Long maxIterations;
  //  private AddonNaturalTextActionData addonNaturalTextActionData;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  @ToString.Exclude
  private List<ForLoopConditionDTO> forLoopConditionDTOs;
  private Boolean disabled;
  private Boolean ignoreStepResult;
  private Long testDataProfileStepId;
  private List<TestStepDTO> testStepDTOS = new ArrayList<>();
  private Integer index;
  private Long testDataId;
  private Integer testDataIndex;
  private String setName;
  private String parentHierarchy;


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
    json.put("value", dataMap);
    json.put("element", element);
    json.put("fromElement", fromElement);
    json.put("toElement", toElement);
    json.put("attribute", attribute);
    json.put("testDataFunctionArgs", testDataFunctionArgs);
    return json.toMap();
  }

  // we are using TestStepDataMap bean object in TestStepResult: stepDetails
  public TestStepDataMap getDataMapBean() {
    TestStepDataMap testStepDataMap = new TestStepDataMap();
    testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
    testStepDataMap.setTestData(dataMap != null ? dataMap.getTestData() : null);
    testStepDataMap.setElement(element);
    testStepDataMap.setFromElement(fromElement);
    testStepDataMap.setToElement(toElement);
    testStepDataMap.setAttribute(attribute);
    testStepDataMap.setForLoop(dataMap != null ? dataMap.getForLoop() : null);
    return testStepDataMap;
  }

  public TestStepRecorderDataMap mapTestData() {
    TestStepRecorderDataMap testStepDataMap = new TestStepRecorderDataMap();
    if(dataMap != null && dataMap.getTestData() != null) {
      for (String key : dataMap.getTestData().keySet()) {
          RecorderTestStepNlpData recorderTestStepNlpData = new RecorderTestStepNlpData();
          recorderTestStepNlpData.setValue(dataMap.getTestData().get(key).getValue());
          recorderTestStepNlpData.setType(dataMap.getTestData().get(key).getType());
          testStepDataMap.setTestData(new HashMap<>() {{
            put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_RECORDER, recorderTestStepNlpData);
          }});
      }
    }
    if(element != null) {
      testStepDataMap.setUiIdentifier(element);
    }
    if(fromElement != null) {
      testStepDataMap.setFromUiIdentifier(fromElement);
    }
    if(toElement != null) {
      testStepDataMap.setToUiIdentifier(toElement);
    }
    if(attribute != null) {
      testStepDataMap.setAttribute(attribute);
    }
    if(ifConditionExpectedResults.length > 0) {
      testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
    }
    return testStepDataMap;
  }
}
