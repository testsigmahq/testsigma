/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestCaseDetails;
import com.testsigma.model.TestCaseStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TestCaseResultDTO {
  private Long id;
  private Long testCaseId;
  private Long testPlanResultId;
  private Long environmentResultId;
  private Long suiteId;
  private String iteration;
  private ResultConstant result;
  private StatusConstant status;
  private Boolean isStepGroup;
  private String message;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private Long suiteResultId;
  private Long parentId;
  private String testDataSetName;
  private Long position;
  private Integer testCaseTypeId;
  private TestCaseStatus testCaseStatus;
  private Integer priorityId;
  private Boolean isDataDriven;
  private Long testDataId;
  private TestCaseDetails testCaseDetails;
  private Long totalCount;
  private Long failedCount;
  private Long passedCount;
  private Long abortedCount;
  private Long stoppedCount;
  private Long notExecutedCount;
  private Long queuedCount;
  private Boolean isVisuallyPassed;
  private TestCaseDTO testCase;
  private TestSuiteDTO testSuite;
  private TestDeviceResultDTO testDeviceResult;
  private TestCaseResultDTO parentResult;
  private String sessionId;
  private TestCaseResultDTO childResult;
  private Long reRunParentId;
  private Timestamp createdDate;
  private Timestamp updatedDate;

}
