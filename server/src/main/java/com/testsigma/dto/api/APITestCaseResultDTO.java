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
import com.testsigma.model.TestCaseStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class APITestCaseResultDTO {
  private Long id;
  @JsonProperty("test_case_id")
  private Long testCaseId;
  @JsonProperty("test_plan_result_id")
  private Long testPlanResultId;
  @JsonProperty("environment_result_id")
  private Long environmentResultId;
  @JsonProperty("suite_id")
  private Long suiteId;
  @JsonProperty("iteration")
  private String iteration;
  @JsonProperty("result")
  private ResultConstant result;
  @JsonProperty("status")
  private StatusConstant status;
  @JsonProperty("is_step_group")
  private Boolean isStepGroup;
  @JsonProperty("message")
  private String message;
  @JsonProperty("start_time")
  private Timestamp startTime;
  @JsonProperty("end_time")
  private Timestamp endTime;
  @JsonProperty("duration")
  private Long duration;
  @JsonProperty("suite_result_id")
  private Long suiteResultId;
  @JsonProperty("parent_id")
  private Long parentId;
  @JsonProperty("test_data_set_name")
  private String testDataSetName;
  @JsonProperty("position")
  private Long position;
  @JsonProperty("test_case_type_id")
  private Integer testCaseTypeId;
  @JsonProperty("test_case_status")
  private TestCaseStatus testCaseStatus;
  @JsonProperty("priority_id")
  private Integer priorityId;
  @JsonProperty("is_data_driven")
  private Boolean isDataDriven;
  @JsonProperty("test_data_id")
  private Long testDataId;
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
  @JsonProperty("parent_result")
  private APITestCaseResultDTO parentResult;
  @JsonProperty("session_id")
  private String sessionId;
  @JsonProperty("child_result")
  private APITestCaseResultDTO childResult;
  @JsonProperty("re_run_parent_id")
  private Long reRunParentId;
  @JsonProperty("created_date")
  private Timestamp createdDate;
  @JsonProperty("updated_date")
  private Timestamp updatedDate;

}
