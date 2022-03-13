package com.testsigma.web.request;

import com.testsigma.constants.AutomatorMessages;
import com.testsigma.exception.ErrorTypes;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
@ToString
public class EnvironmentRunResultRequest {
  List<TestCaseResultRequest> testcaseResults = new ArrayList<TestCaseResultRequest>();
  List<TestSuiteResultRequest> groupResults = new ArrayList<TestSuiteResultRequest>();
  private Long environmentId;
  private Long id;
  private Timestamp startTime;
  private Timestamp endTime;
  private ResultConstant result;
  private StatusConstant status;
  private String message;
  private Long duration;
  private Timestamp agentStartTime;
  private Timestamp agentEndTime;
  private Integer errorCode;
  private Timestamp executionInitiatedOn;
  private Timestamp agentPickedOn;
  private Timestamp deviceAllocatedOn;
  private Timestamp sessionCreatedOn;
  private Timestamp sessionCompletedOn;
  private Map<String, Object> metaData = new HashMap<String, Object>();

  public String getBrowserVersion() {
    return (String) this.getMetaData().get("browser_version_found");
  }

  public String getMessage() {
    if (ErrorTypes.USER_STOPPED_EXECUTION.equals(this.errorCode)) {
      return AutomatorMessages.MSG_USER_ABORTED_EXECUTION;
    }
    return this.message;
  }
}
