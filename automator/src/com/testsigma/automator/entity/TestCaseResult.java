package com.testsigma.automator.entity;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
public class TestCaseResult {
  List<TestCaseStepResult> testCaseStepResults = new ArrayList<>();
  List<TestCaseResult> testCaseResults = new ArrayList<>();
  private Long id = 0L;
  private String testCaseName;
  private Long testCaseId;
  private Long envRunId;
  private Long groupId;
  private ResultConstant result;
  private Boolean isStepGroup = false;
  private String message;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private Long groupResultId;
  private Long parentId;
  private String testDataSetName;
  private Long testDataId;
  private Boolean dataDriven = false;
  private Integer errorCode;
  private Integer currentIndex;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private Long testPlanResultId;
  private Long phoneNumberId;

  public TestCaseResult() {
  }

  public TestCaseResult(Long testCaseId) {
    this.testCaseId = testCaseId;
  }
}
