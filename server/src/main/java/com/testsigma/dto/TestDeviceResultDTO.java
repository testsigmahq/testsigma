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
public class TestDeviceResultDTO {
  private Long id;
  private Long testDeviceId;
  private TestDeviceDTO testDevice;
  private TestPlanResultDTO testPlanResult;
  private Boolean started;
  private TestDeviceSettingsDTO testDeviceSettings;
  private String browserVersion;
  private StatusConstant status;
  private ResultConstant result;
  private Long duration;
  private Timestamp endTime;
  private Timestamp startTime;
  private Long totalCount;
  private Long failedCount;
  private Long passedCount;
  private Long abortedCount;
  private Long stoppedCount;
  private Long notExecutedCount;
  private Long queuedCount;
  private Boolean isVisuallyPassed;
  private Timestamp executionInitiatedOn;
  private Timestamp agentPickedOn;
  private Timestamp deviceAllocatedOn;
  private Timestamp sessionCreatedOn;
  private Timestamp sessionCompletedOn;
  private String message;
  private TestDeviceResultDTO childResult;
  private Long reRunParentId;
  private Timestamp createdDate;
  private Timestamp updatedDate;

}
