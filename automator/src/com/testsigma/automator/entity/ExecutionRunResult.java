package com.testsigma.automator.entity;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;

@Data
@Log4j2
public class ExecutionRunResult {
  private Long executionRunId;
  private Timestamp startTime;
  private Timestamp endTime;
  private Integer status;
  private Long duration;
  private String message;

  public ExecutionRunResult(Long executionRunId, Timestamp startTime, Timestamp endTime, Integer status,
                            String message) {
    this.executionRunId = executionRunId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
    this.message = message;
  }

  public ExecutionRunResult() {
  }
}
