/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.automator.entity.ElementPropertiesEntity;
import com.testsigma.automator.entity.TestDataPropertiesEntity;
import com.testsigma.model.KibbutzElementData;
import com.testsigma.model.KibbutzTestStepTestData;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestStepPriority;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Map;

@Data
public class TestStepResultDTO {
  private Long id;
  private Long envRunId;
  private Long testCaseId;
  private Long stepId;
  private Long stepGroupId;
  private ResultConstant result;
  private Integer errorCode;
  private String message;
  private StepResultMetadataDTO metadata;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private Long testCaseResultId;
  private Long groupResultId;
  private String screenshotName;
  private Long parentResultId;
  @ToString.Exclude
  private String webDriverException;
  private TestStepPriority priority;
  private StepDetailsDTO stepDetails;
  private Map<String, TestDataPropertiesEntity> testDataDetails;
  private Map<String, ElementPropertiesEntity> fieldDefinitionDetails;
  private String screenShotURL;
  private Integer waitTime;
  private Map<String, KibbutzTestStepTestData> kibbutzTestData;
  private Map<String, KibbutzElementData> kibbutzElements;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private String kibbutzActionLogs;
}
