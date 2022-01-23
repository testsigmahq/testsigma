/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import com.testsigma.model.ScheduleStatus;
import com.testsigma.model.ScheduleType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ScheduleTestPlanDTO {
  private Long id;
  private Long testPlanId;
  private String name;
  private String comments;
  private ScheduleType scheduleType;
  private String scheduleTime;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private ScheduleStatus status;

}
