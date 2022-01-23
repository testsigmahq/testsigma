/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class APITestDeviceResultDTO {
  private Long id;
  @JsonProperty("testDevice_id")
  private Long testDeviceId;
  @JsonProperty("test_plan_result")
  private Long testPlanResultId;
  @JsonProperty("started")
  private Boolean started;
  @JsonProperty("browser_version")
  private String browserVersion;
  @JsonProperty("status")
  private StatusConstant status;
  @JsonProperty("result")
  private ResultConstant result;
  @JsonProperty("duration")
  private Long duration;
  @JsonProperty("end_time")
  private Timestamp endTime;
  @JsonProperty("start_time")
  private Timestamp startTime;
  @JsonProperty("total_count")
  private Long totalCount;
  @JsonProperty("failed_count")
  private Long failedCount;
  @JsonProperty("passed_count")
  private Long passedCount;
  @JsonProperty("aborted_count")
  private Long abortedCount;
  @JsonProperty("stopped_count")
  private Long stoppedCount;
  @JsonProperty("not_executed_count")
  private Long notExecutedCount;
  @JsonProperty("queued_count")
  private Long queuedCount;
  @JsonProperty("is_visually_passed")
  private Boolean isVisuallyPassed;
  @JsonProperty("execution_initiated_on")
  private Timestamp executionInitiatedOn;
  @JsonProperty("agent_picked_on")
  private Timestamp agentPickedOn;
  @JsonProperty("device_allocated_on")
  private Timestamp deviceAllocatedOn;
  @JsonProperty("session_created_on")
  private Timestamp sessionCreatedOn;
  @JsonProperty("session_completed_on")
  private Timestamp sessionCompletedOn;
  @JsonProperty("message")
  private String message;
  @JsonProperty("child_result")
  private APITestDeviceResultDTO childResult;
  @JsonProperty("re_run_parent_id")
  private Long reRunParentId;
  @JsonProperty("created_date")
  private Timestamp createdDate;
  @JsonProperty("updated_date")
  private Timestamp updatedDate;

}
