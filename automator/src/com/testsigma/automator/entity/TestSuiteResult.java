package com.testsigma.automator.entity;

import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
@ToString
public class TestSuiteResult {
  List<TestCaseResult> testCaseResults = new ArrayList<TestCaseResult>();
  private Long id;
  private Long envRunId;
  private Long groupId;
  private Timestamp startTime;
  private Timestamp endTime;
  private ResultConstant result;
  private String message;
  private Long duration;
  private Timestamp executionInitiatedOn;
  private Timestamp agentPickedOn;
  private Timestamp deviceAllocatedOn;
  private Timestamp sessionCreatedOn;
  private Timestamp sessionCompletedOn;
  private Timestamp createdDate;
  private Timestamp updatedDate;
}
