/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.testsigma.automator.entity.TestDataPropertiesEntity;

import java.util.*;

@Data
@ToString
@EqualsAndHashCode
public class TestCaseStepEntityDTO implements Cloneable {
  public LinkedHashMap<String, TestDataPropertiesEntity> testDataMap;
  public Map<String, ElementPropertiesDTO> elementsMap;
  public Map<String, AttributePropertiesEntityDTO> attributesMap;
  public AddonNaturalTextActionEntityDTO addonNaturalTextActionEntity;
  public List<AddonPluginTestDataFunctionEntityDTO> addonPluginTDFEntityList;
  private Long id;
  private ResultConstant[] ifConditionExpectedResults;
  private Long testCaseId;
  private Long testPlanId;
  private Long preRequisite;
  private Long parentId;
  private TestStepPriority priority;
  private TestStepType type;
  private Integer waitTime;
  private Long stepGroupId;
  private Integer index;
  private String screenshotPath;
  private Integer naturalTextActionId;
  private String keyword;
  private TestStepConditionType conditionType;
  private Integer position;
  private String element;
  private String elementName;
  private String locatorStrategy;
  private String testDataType;
  private String testDataName;
  private String testDataValue;
  private String testDataValuePreSignedURL;
  private String attribute;
  private String iteration;
  private String testDataProfileName;
  private String action;
  private Boolean snippetEnabled;
  private Boolean disabled;
  private String snippetClass;
  private Boolean ignoreStepResult;
  private StepDetailsDTO stepDetails;
  private Map<String, AddonTestStepTestData> addonTestData;
  private Map<String, AddonElementData> addonElements;
  private Map<String, Object> additionalData;
  private Boolean visualEnabled = false;
  private List<TestCaseStepEntityDTO> testCaseSteps = new ArrayList<>();
  private Map<String, String> additionalScreenshotPaths = new HashMap<>();

  public TestCaseStepEntityDTO clone() throws CloneNotSupportedException {
    TestCaseStepEntityDTO entity = (TestCaseStepEntityDTO) super.clone();
    List<TestCaseStepEntityDTO> steps = new ArrayList<>();
    for (TestCaseStepEntityDTO step : testCaseSteps) {
      steps.add(step.clone());
    }
    entity.setTestCaseSteps(steps);
    return entity;
  }
}
