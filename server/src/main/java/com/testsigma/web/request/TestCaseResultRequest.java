package com.testsigma.web.request;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Log4j2
@ToString
public class TestCaseResultRequest {
  List<TestStepResultRequest> testCaseStepResults = new ArrayList<TestStepResultRequest>();
  List<TestCaseResultRequest> testCaseResults = new ArrayList<TestCaseResultRequest>();
  private Long id = 0L;
  private Long testCaseId;
  private Long envRunId;
  private Long groupId;
  private ResultConstant result;
  private StatusConstant status;
  private Boolean isStepGroup;
  private String message;
  private Timestamp startTime;
  private Timestamp endTime;
  private Long duration;
  private Long groupResultId;
  private Long parentId;
  private String testDataSetName;
  private Long testDataId;
  private Boolean dataDriven;
  private Integer errorCode;
  private Integer currentIndex;
  private String sessionId;
  private boolean visualTestingEnabled = false;


  public TestCaseResultRequest() {
  }

  public TestCaseResultRequest(Long testCaseId) {
    this.testCaseId = testCaseId;
  }
}
