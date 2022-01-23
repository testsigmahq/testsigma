package com.testsigma.automator.entity;

import com.testsigma.automator.constants.EnvSettingsConstants;
import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.AutomatorMessages;
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
public class EnvironmentRunResult {
  List<TestCaseResult> testcaseResults = new ArrayList<TestCaseResult>();
  List<TestSuiteResult> groupResults = new ArrayList<TestSuiteResult>();
  private Long environmentId;
  private Long id;
  private Timestamp startTime;
  private Timestamp endTime;
  private ResultConstant result;
  private String message;
  private Long duration;
  private Integer errorCode;
  private Timestamp executionInitiatedOn;
  private Timestamp agentPickedOn;
  private Timestamp deviceAllocatedOn;
  private Timestamp sessionCreatedOn;
  private Timestamp sessionCompletedOn;
  private Boolean runInParallel = false;
  private Map<String, Object> metaData = new HashMap<String, Object>();

  public EnvironmentRunResult() {
  }

  public EnvironmentRunResult(Long environmentId) {
    this.environmentId = environmentId;
  }

  public String getBrowserVersion() {
    return (String) this.getMetaData().get(EnvSettingsConstants.KEYS_BROWSER_VERSION_FOUND);
  }

  public String getMessage() {
    Integer error = this.errorCode;
    if (ErrorCodes.USER_STOPPED_EXECUTION.equals(error)) {
      return AutomatorMessages.MSG_USER_ABORTED_EXECUTION;
    }
    return this.message;
  }
}
