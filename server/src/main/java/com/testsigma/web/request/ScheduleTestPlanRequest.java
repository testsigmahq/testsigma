/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.testsigma.model.ScheduleStatus;
import com.testsigma.model.ScheduleType;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Log4j2
public class ScheduleTestPlanRequest {
  private Long id;
  private Long testPlanId;
  private String name;
  private String comments;
  private ScheduleType scheduleType;
  private String scheduleTime;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private ScheduleStatus status;

  public Timestamp getScheduleTime() {
    try {
      SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      Date date = format.parse(this.scheduleTime);
      return new java.sql.Timestamp(date.getTime());
    } catch (Exception e) {
      log.debug("Problem while parsing scheduleTime for :" + this.scheduleTime);
      log.error(e.getMessage(), e);
    }
    return null;
  }
}
