/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.model.recorder.RecorderTestStepNlpData;
import com.testsigma.model.recorder.TestStepRecorderDataMap;
import com.testsigma.model.recorder.TestStepRecorderForLoop;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.*;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "test_steps")
@Data
@Log4j2
public class TestStep {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  private TestStepPriority priority;

  @Column(name = "step_id")
  private Integer position;

  @Column(name = "pre_requisite")
  private Long preRequisiteStepId;

  @Column(name = "action", columnDefinition = "text")
  private String action;

  @Column(name = "test_case_id")
  private Long testCaseId;

  @Type(type = "json")
  @Column(name = "addon_test_data", columnDefinition = "json")
  private String addonTestData;

  @Type(type = "json")
  @Column(name = "addon_elements", columnDefinition = "json")
  private String addonElements;

  @Column(name = "addon_action_id")
  private Long addonActionId;

  @Column(name = "step_group_id")
  private Long stepGroupId;

  @Type(type = "json")
  @Column(name = "condition_if")
  private ResultConstant[] ifConditionExpectedResults;

  @Column(name = "test_data")
  private String dataMap;

  @Column(name = "element")
  private String element;

  @Column(name = "attribute")
  private String attribute;

  @Column(name = "addon_test_data_function")
  private String addonTDF;

  @Transient
  private String fromElement;

  @Transient
  private String toElement;

  @Transient
  private ForLoopCondition forLoopConditions;

  @Column(name = "test_data_function_id")
  private Long testDataFunctionId;

  @Type(type = "json")
  @Column(name = "test_data_function_args")
  private Map<String, String> testDataFunctionArgs;

  @Column(name = "natural_text_action_id")
  private Integer naturalTextActionId;

  @Column(name = "max_iterations")
  private Long maxIterations;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private TestStepType type;

  @Column(name = "wait_time")
  private Integer waitTime;

  @Column(name = "condition_type")
  @Enumerated(EnumType.STRING)
  private TestStepConditionType conditionType;

  @Column(name = "parent_id")
  private Long parentId;

  @Column(name = "copied_from")
  private Long copiedFrom;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Nullable
  @Column(name = "disabled")
  private Boolean disabled;

  @Nullable
  @Column(name = "ignore_step_result")
  private Boolean ignoreStepResult;

  @Column(name = "imported_id")
  private Long importedId;

  @Column(name = "visual_enabled")
  private Boolean visualEnabled = false;

  @Column(name = "test_data_profile_step_id")
  private Long testDataProfileStepId;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "test_case_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestCase testCase;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestStep parentStep;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "addon_action_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private AddonNaturalTextAction addonNaturalTextAction;

  @OneToMany(mappedBy = "testStep")
  @Fetch(value = FetchMode.SELECT)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<EntityExternalMapping> entityExternalMapping;

  @ManyToOne
  @Fetch(value = FetchMode.SELECT)
  @JoinColumn(name = "step_group_id", referencedColumnName = "id", insertable = false, updatable = false)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private TestCase stepGroup;

  @OneToOne(mappedBy = "testStep")
  @Fetch(value = FetchMode.SELECT)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private RestStep restStep;

  public Map<String, AddonTestStepTestData> getAddonTestData() {
    ObjectMapperService mapper = new ObjectMapperService();
    return mapper.parseJson(this.addonTestData, new TypeReference<>() {
    });
  }

  public void setAddonTestData(Map<String, AddonTestStepTestData> testData) {
    ObjectMapperService mapper = new ObjectMapperService();
    addonTestData = mapper.convertToJson(testData);
  }

  public Map<String, AddonElementData> getAddonElements() {
    ObjectMapperService mapper = new ObjectMapperService();
    return mapper.parseJson(this.addonElements, new TypeReference<>() {
    });
  }

  public void setAddonElements(Map<String, AddonElementData> elements) {
    ObjectMapperService mapper = new ObjectMapperService();
    addonElements = mapper.convertToJson(elements);
  }


  public AddonTestStepTestData getAddonTDF() {
    return new ObjectMapperService().parseJson(addonTDF, AddonTestStepTestData.class);
  }

  public void setAddonTDF(AddonTestStepTestData addonTdf) {
    this.addonTDF = (addonTdf == null) ? null : new ObjectMapperService().convertToJson(addonTdf);
  }

  public TestStepDataMap getDataMapBean() {
    TestStepDataMap testStepDataMap = new TestStepDataMap();
    testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
    testStepDataMap.setElement(element);
    testStepDataMap.setFromElement(fromElement);
    testStepDataMap.setToElement(toElement);
    testStepDataMap.setAttribute(attribute);
    testStepDataMap.setVisualEnabled(visualEnabled);
    ObjectMapperService mapper = new ObjectMapperService();
    if(dataMap != null) {
      TestStepData testStepData = mapper.parseJson(this.dataMap, TestStepData.class);
      if (testStepData != null) {
        testStepDataMap.setTestData(testStepData.getTestData());
      }
    }
    DefaultDataGeneratorsDetails functionDetails = new DefaultDataGeneratorsDetails();
    functionDetails.setId(testDataFunctionId);
    functionDetails.setArguments(testDataFunctionArgs);
    return testStepDataMap;
  }

  public TestStepRecorderDataMap getRecorderDataMap() throws Exception {
    ObjectMapperService mapperService = new ObjectMapperService();
    TestStepRecorderDataMap testStepDataMap = new TestStepRecorderDataMap();
    if(dataMap != null) {
      TestStepData testStepData = mapperService.parseJsonModel(this.dataMap, TestStepData.class);
      if(testStepData != null && testStepData.getTestData() != null) {
        for (String key : testStepData.getTestData().keySet()) {
          RecorderTestStepNlpData recorderTestStepNlpData = new RecorderTestStepNlpData();
          recorderTestStepNlpData.setValue(testStepData.getTestData().get(key).getValue());
          recorderTestStepNlpData.setType(testStepData.getTestData().get(key).getType());
          testStepDataMap.setTestData(new HashMap<>() {{
            put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA_RECORDER, recorderTestStepNlpData);
          }});
        }
      }
    }
    if (element != null) {
      testStepDataMap.setUiIdentifier(element);
    }
    if(fromElement != null) {
      testStepDataMap.setFromUiIdentifier(fromElement);
    }
    if(toElement != null) {
      testStepDataMap.setToUiIdentifier(toElement);
    }
    if (ifConditionExpectedResults != null && ifConditionExpectedResults.length > 0) {
      testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
    }
    return testStepDataMap;
  }

  public TestStepDataMap getDataMap() {
    ObjectMapperService mapperService = new ObjectMapperService();
    Map<String, Map<String, Map<String, String>>> map = mapperService.parseJson(dataMap, Map.class);
    TestStepDataMap testStepDataMap = new TestStepDataMap();
    log.info("Parsing json to map: " + map);
    if(map!= null && map.containsKey("test-data")) {
      Map<String, TestStepNlpData> testData = new HashMap<>();
      for(String key : map.get("test-data").keySet()) {
        TestStepNlpData testStepNlpData = new TestStepNlpData();
        testStepNlpData.setValue(map.get("test-data").get(key).get("value"));
        testStepNlpData.setType(map.get("test-data").get(key).get("type"));
        if(map.get("test-data").get(key).containsKey("testDataFunction")) {
          testStepNlpData.setTestDataFunction(new ObjectMapper().convertValue(map.get("test-data").get(key).get("testDataFunction"), DefaultDataGeneratorsEntity.class));
        }
        if(map.get("test-data").get(key).containsKey("addonTDF")) {
          testStepNlpData.setAddonTDF(new ObjectMapper().convertValue(map.get("test-data").get(key).get("addonTDF"), AddonTestStepTestData.class));
        }
        testData.put(key, testStepNlpData);
      }
      testStepDataMap.setTestData(testData);
      if (element != null) {
        testStepDataMap.setElement(element);
      }
      if(fromElement != null) {
        testStepDataMap.setFromElement(fromElement);
      }
      if(toElement != null) {
        testStepDataMap.setToElement(toElement);
      }
      if (ifConditionExpectedResults != null && ifConditionExpectedResults.length > 0) {
        testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
      }
    }
    log.info("Parsed json to testStepDataMap: " + testStepDataMap);
    return testStepDataMap;
  }

  public void setDataMap(TestStepDataMap testStepDataMap) {
    this.dataMap = new ObjectMapperService().convertToJson(testStepDataMap);
  }

  public String getStringDataMap() {
    return dataMap;
  }

}
