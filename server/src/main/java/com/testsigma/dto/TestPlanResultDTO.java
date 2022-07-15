/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@EqualsAndHashCode
public class TestPlanResultDTO {
  private Long id;
  private TestPlanDTO testPlan;
  private DryTestPlanDTO dryTestPlan;
  private Long testPlanId;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private ResultConstant result;
  private StatusConstant status;
  private String message;
  private String buildNo;
  private Long environmentId;
  private Long totalCount;
  private Long failedCount;
  private Long passedCount;
  private Long abortedCount;
  private Long stoppedCount;
  private Long notExecutedCount;
  private Long queuedCount;
  private Boolean isVisuallyPassed;
  private EnvironmentDTO environment;
  private TestPlanResultDTO childResult;
  private Long reRunParentId;
  private ReRunType reRunType;
  private ExecutionTriggeredType triggeredType;
  private int totalRunningCount;
  private TestPlanDetails testPlanDetails;
  private int totalQueuedCount;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private TestPlanLabType testPlanLabType;
  private Long applicationVersionId;
  private Long targetMachineId;
  private Long preRequisiteEnvironmentResultId;

}
