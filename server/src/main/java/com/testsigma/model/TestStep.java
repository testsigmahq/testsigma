/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
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
}
