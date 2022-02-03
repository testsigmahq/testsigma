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
public class APITestSuiteResultDTO {
  private Long id;
  @JsonProperty("environment_result_id")
  private Long environmentResultId;
  @JsonProperty("suite_id")
  private Long suiteId;
  @JsonProperty("start_time")
  private Timestamp startTime;
  @JsonProperty("end_time")
  private Timestamp endTime;
  @JsonProperty("duration")
  private Long duration;
  @JsonProperty("result")
  private ResultConstant result;
  @JsonProperty("status")
  private StatusConstant status;
  @JsonProperty("message")
  private String message;
  @JsonProperty("position")
  private Long position;
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
  @JsonProperty("session_id")
  private String sessionId;
  @JsonProperty("child_result")
  private APITestSuiteResultDTO childResult;
  @JsonProperty("re_run_parent_id")
  private Long reRunParentId;
  @JsonProperty("created_date")
  private Timestamp createdDate;
  @JsonProperty("updated_date")
  private Timestamp updatedDate;

}
