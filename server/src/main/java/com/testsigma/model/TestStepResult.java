/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "test_step_results")
@Data
@Log4j2
public class TestStepResult {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "test_device_result_id")
  private Long envRunId;

  @Column(name = "test_case_id")
  private Long testCaseId;

  @Column(name = "step_id")
  private Long stepId;

  @Column(name = "step_group_id")
  private Long stepGroupId;

  @Column(name = "result")
  @Enumerated(EnumType.STRING)
  private ResultConstant result;

  @Column(name = "error_code")
  private Integer errorCode;

  @Column(name = "message")
  private String message;

  @Column(name = "metadata")
  private String metadata;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "end_time")
  private Timestamp endTime;

  @Column(name = "duration")
  private Long duration;

  @Column(name = "test_case_result_id")
  private Long testCaseResultId;

  @Column(name = "step_group_result_id")
  private Long groupResultId;

  @Column(name = "screenshot_name")
  private String screenshotName;

  @Column(name = "parent_result_id")
  private Long parentResultId;

  @Column(name = "web_driver_exception")
  @ToString.Exclude
  private String webDriverException;

  @Column(name = "priority")
  @Enumerated(EnumType.STRING)
  private TestStepPriority priority;

  @Column(name = "test_step_details")
  private String stepDetails;

  @Column(name = "wait_time")
  private Integer waitTime;

  @Type(type = "json")
  @Column(name = "addon_test_data")
  private String addonTestData;

  @Type(type = "json")
  @Column(name = "addon_elements")
  private String addonElements;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column(name = "addon_action_logs")
  private String addonActionLogs;

  @Column(name = "element_details")
  private String ElementDetails;

  @Column(name = "test_data_details")
  private String testDataDetails;

  @Column(name = "visual_enabled")
  private Boolean visualEnabled = false;

  @OneToMany(mappedBy = "stepResult", fetch = FetchType.LAZY)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private Set<SuggestionResultMapping> suggestionResultMappings;

  @Transient
  private String screenShotURL;

  public Map<String, AddonTestStepTestData> getAddonTestData() {
    ObjectMapperService mapper = new ObjectMapperService();
    return mapper.parseJson(this.addonTestData, new TypeReference<Map<String, AddonTestStepTestData>>() {
    });
  }

  public void setAddonTestData(Map<String, AddonTestStepTestData> testData) {
    ObjectMapperService mapper = new ObjectMapperService();
    addonTestData = mapper.convertToJson(testData);
  }

  public Map<String, AddonElementData> getAddonElements() {
    ObjectMapperService mapper = new ObjectMapperService();
    return mapper.parseJson(this.addonElements, new TypeReference<Map<String, AddonElementData>>() {
    });
  }

  public void setAddonElements(Map<String, AddonElementData> elements) {
    ObjectMapperService mapper = new ObjectMapperService();
    addonElements = mapper.convertToJson(elements);
  }

  public StepResultMetadata getMetadata() {

    return new ObjectMapperService().parseJson(metadata, StepResultMetadata.class);
  }

  public void setMetadata(StepResultMetadata metadata) {

    this.metadata = (metadata == null) ? null : new ObjectMapperService().convertToJson(metadata);
  }

  public StepDetails getStepDetails() {
    return new ObjectMapperService().parseJson(stepDetails, StepDetails.class);
  }

  public void setStepDetails(StepDetails stepDetails) {
    this.stepDetails = new ObjectMapperService().convertToJson(stepDetails);
  }

  public Map<String, ElementPropertiesEntity> getElementDetails() {
    return new ObjectMapperService().parseJson(ElementDetails, Map.class);
  }

  public void setElementDetails(Map<String, ElementPropertiesEntity> data) {
    this.ElementDetails = data == null ? null : new ObjectMapperService().convertToJson(data);
  }

  public Map<String, TestDataPropertiesEntity> getTestDataDetails() {
    return new ObjectMapperService().parseJson(testDataDetails, Map.class);
  }

  public void setTestDataDetails(Map<String, TestDataPropertiesEntity> data) {
    this.testDataDetails = data == null ? null : new ObjectMapperService().convertToJson(data);
  }
}
