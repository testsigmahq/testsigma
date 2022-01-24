package com.testsigma.web.request;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;

@Data
@Log4j2
@ToString
public class TestSuiteResultRequest {
  private Long id;
  private Long envRunId;
  private Long groupId;
  private Timestamp startTime;
  private Timestamp endTime;
  private ResultConstant result;
  private String message;
  private Long duration;
  private StatusConstant status;
  private String sessionId;
  private Boolean suiteInParallel = Boolean.FALSE;
  private Timestamp executionInitiatedOn;
  private Timestamp agentPickedOn;
  private Timestamp deviceAllocatedOn;
  private Timestamp sessionCreatedOn;
  private Timestamp sessionCompletedOn;
}
