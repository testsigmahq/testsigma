/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TestSuiteResultDTO {
  private Long id;
  private Long environmentResultId;
  private Long suiteId;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private ResultConstant result;
  private StatusConstant status;
  private String message;
  private Long position;
  private Timestamp executionInitiatedOn;
  private Timestamp agentPickedOn;
  private Timestamp deviceAllocatedOn;
  private Timestamp sessionCreatedOn;
  private Timestamp sessionCompletedOn;
  private Long totalCount;
  private Long failedCount;
  private Long passedCount;
  private Long abortedCount;
  private Long stoppedCount;
  private Long notExecutedCount;
  private Long queuedCount;
  private Boolean isVisuallyPassed;
  private TestSuiteDTO testSuite;
  private TestDeviceResultDTO testDeviceResult;
  private String sessionId;
  private TestSuiteResultDTO childResult;
  private Long reRunParentId;
  private Timestamp createdDate;
  private Timestamp updatedDate;

}
