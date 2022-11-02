/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.export.CloudTestDataFunction;
import com.testsigma.model.recorder.KibbutzTestStepTestData;
import com.testsigma.model.recorder.TestStepNlpData;
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
  private String testData;

  @Column(name = "test_data_type")
  private String testDataType;

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

  @Column(name = "for_loop_start_index")
  private Integer forLoopStartIndex;

  @Column(name = "for_loop_end_index")
  private Integer forLoopEndIndex;

  @Column(name = "for_loop_test_data_id")
  private Long forLoopTestDataId;

  @Column(name = "test_data_function_id")
  private Long testDataFunctionId;

  @Type(type = "json")
  @Column(name = "test_data_function_args")
  private Map<String, String> testDataFunctionArgs;

  @Column(name = "natural_text_action_id")
  private Integer naturalTextActionId;

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
    testStepDataMap.setTestData(testData);
    testStepDataMap.setTestDataType(testDataType);
    testStepDataMap.setElement(element);
    testStepDataMap.setFromElement(fromElement);
    testStepDataMap.setToElement(toElement);
    testStepDataMap.setAttribute(attribute);
    testStepDataMap.setVisualEnabled(visualEnabled);
    ObjectMapperService mapper = new ObjectMapperService();
    testStepDataMap.setAddonTDF(mapper.parseJson(addonTestData, AddonTestStepTestData.class));
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

  public TestStepRecorderDataMap getDataMap() {
    ObjectMapperService mapperService = new ObjectMapperService();
    TestStepRecorderDataMap testStepDataMap;
    try {
      testStepDataMap = mapperService.parseJsonModel(testData, TestStepRecorderDataMap.class);
    } catch (Exception e) {
      //Map<String, String> map = mapperService.parseJson(testData, Map.class);
      testStepDataMap = new TestStepRecorderDataMap();
      TestStepNlpData testStepNlpData = new TestStepNlpData();
      testStepNlpData.setValue(testData);
      testStepNlpData.setType(testDataType);
      testStepDataMap.setTestData(new HashMap<>() {{
        put(NaturalTextActionConstants.TEST_STEP_DATA_MAP_KEY_TEST_DATA, testStepNlpData);
      }});
    }
    if (testStepDataMap == null) {
      testStepDataMap = new TestStepRecorderDataMap();
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
    if (ifConditionExpectedResults.length > 0) {
      testStepDataMap.setIfConditionExpectedResults(ifConditionExpectedResults);
    }
    if (forLoopStartIndex != null || forLoopTestDataId != null || forLoopEndIndex != null) {
      TestStepRecorderForLoop testStepForLoop= new TestStepRecorderForLoop();
      testStepForLoop.setTestDataId(forLoopTestDataId);
      testStepForLoop.setStartIndex(forLoopStartIndex);
      testStepForLoop.setEndIndex(forLoopEndIndex);
      testStepDataMap.setForLoop(testStepForLoop);
    }
    return testStepDataMap;
  }

  /*
  public TestStepRecorderDataMap getDataMap() {
    ObjectMapperService mapperService = new ObjectMapperService();
    try {
      return mapperService.parseJsonModel(testData, TestStepRecorderDataMap.class);
    }
    catch(Exception e) {
      Map<String, String> map = mapperService.parseJson(testData, Map.class);
      TestStepRecorderDataMap testStepDataMap = new TestStepRecorderDataMap();
      log.info("Parsing json to map: " + map);
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
      log.info("Parsed json to testStepDataMap: " + testStepDataMap);
      return testStepDataMap;
    }
  }*/

  public void setTestDataType(String testDataType) {
    if (testDataType !=null){
      if (testDataType.equals("global"))
        this.testDataType = TestDataType.environment.getDispName();
      else if (testDataType.equals("phone_number") || testDataType.equals("mail_box"))
        this.testDataType = TestDataType.raw.getDispName();
      else this.testDataType = testDataType;
    }
  }
}
