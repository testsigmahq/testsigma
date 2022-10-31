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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.export.CloudTestDataFunction;
import com.testsigma.model.*;
import com.testsigma.model.recorder.KibbutzTestStepTestData;
import com.testsigma.model.recorder.TestStepNlpData;
import com.testsigma.model.recorder.TestStepRecorderDataMap;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
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
  private String testData;
  private String testDataType;
  private String attribute;
  private String element;
  private String fromElement;
  private String toElement;
  private Integer forLoopStartIndex;
  private Integer forLoopEndIndex;
  private Long forLoopTestDataId;
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
  //  private AddonNaturalTextActionData addonNaturalTextActionData;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  private Boolean disabled;
  private Boolean ignoreStepResult;
  private Long testDataProfileStepId;
  private List<TestStepDTO> testStepDTOS = new ArrayList<>();
  private Integer index;
  private Long testDataId;
  private Integer testDataIndex;
  private String setName;


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

  public TestStepRecorderDataMap mapTestData() {
    ObjectMapperService mapperService = new ObjectMapperService();
    try {
      return mapperService.parseJsonModel(testData, TestStepRecorderDataMap.class);
    }
    catch(Exception e) {
      Map<String, String> map = mapperService.parseJson(testData, Map.class);
      TestStepRecorderDataMap testStepDataMap = new TestStepRecorderDataMap();
      if(map.containsKey("test-data")) {
        TestStepNlpData testStepNlpData = new TestStepNlpData();
        testStepNlpData.setValue(map.get("test-data"));
        testStepNlpData.setType(map.get("test-data-type"));
        if(map.containsKey("test-data-function")) {
          testStepNlpData.setTestDataFunction(new ObjectMapper().convertValue(map.get("test-data-function"), CloudTestDataFunction.class));
        }
        if(map.containsKey("kibbutz_test_data_function")) {
          testStepNlpData.setKibbutzTDF(new ObjectMapper().convertValue(map.get("kibbutz_test_data_function"), KibbutzTestStepTestData.class));
        }
        testStepDataMap.setTestData(new HashMap<>() {{
          put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, testStepNlpData);
        }});
      }
      if(map.containsKey("condition_if")) {
        testStepDataMap.setIfConditionExpectedResults(map.get("condition_if"));
      }
      if(map.containsKey("condition-type")) {
        testStepDataMap.setIfConditionExpectedResults(map.get("condition-type"));
      }
      if(map.containsKey("custom-step")) {
        testStepDataMap.setCustomStep(new ObjectMapper().convertValue(map.get("custom-step"), TestStepCustomStep.class));
      }
      if(map.containsKey("ui-identifier")) {
        testStepDataMap.setUiIdentifier(map.get("ui-identifier"));
      }
      if(map.containsKey("from-ui-identifier")) {
        testStepDataMap.setFromUiIdentifier(map.get("from-ui-identifier"));
      }
      if(map.containsKey("to-ui-identifier")) {
        testStepDataMap.setToUiIdentifier(map.get("to-ui-identifier"));
      }
      if(map.containsKey("attribute")) {
        testStepDataMap.setAttribute(map.get("attribute"));
      }
      if(map.containsKey("for_loop")) {
        testStepDataMap.setForLoop(new ObjectMapper().convertValue(map.get("for_loop"), TestStepForLoop.class));
      }
      if(map.containsKey("while_loop")) {
        testStepDataMap.setWhileLoop(new ObjectMapper().convertValue(map.get("while_loop"), TestStepWhileLoop.class));
      }
      if(map.containsKey("whileCondition")) {
        testStepDataMap.setWhileCondition(map.get("whileCondition"));
      }
      return testStepDataMap;
    }
  }
}
