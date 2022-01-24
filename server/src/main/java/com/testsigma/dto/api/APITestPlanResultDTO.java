/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@EqualsAndHashCode
public class APITestPlanResultDTO {
  private Long id;
  @JsonProperty("dry_test_plan")
  private Long dryTestPlanId;
  @JsonProperty("test_plan_id")
  private Long testPlanId;
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
  @JsonProperty("build_no")
  private String buildNo;
  @JsonProperty("environment_id")
  private Long environmentId;
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
  @JsonProperty("test_device_id")
  private Long testDeviceId;
  @JsonProperty("child_result")
  private APITestPlanResultDTO childResult;
  @JsonProperty("re_run_parent_id")
  private Long reRunParentId;
  @JsonProperty("re_run_type")
  private ReRunType reRunType;
  @JsonProperty("triggered_type")
  private ExecutionTriggeredType triggeredType;
  @JsonProperty("total_running_count")
  private int totalRunningCount;
  @JsonProperty("test_plan_details")
  private TestPlanDetails testPlanDetails;
  @JsonProperty("total_queued_count")
  private int totalQueuedCount;
  @JsonProperty("created_date")
  private Timestamp createdDate;
  @JsonProperty("updated_date")
  private Timestamp updatedDate;

}
