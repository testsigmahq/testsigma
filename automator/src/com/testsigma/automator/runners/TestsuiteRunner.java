package com.testsigma.automator.runners;

import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.constants.DriverSessionType;
import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.exceptions.TestsigmaNoParallelRunException;
import com.testsigma.automator.http.HttpClient;
import com.testsigma.automator.utilities.ErrorUtil;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Data
public abstract class TestsuiteRunner {
  protected Map<Long, TestSuiteResult> groupResultMap = new HashMap<Long, TestSuiteResult>();
  protected Map<Long, TestCaseResult> testcaseResultMap = new HashMap<Long, TestCaseResult>();
  protected Map<Long, TestCaseStepResult> mapStepResult = new HashMap<Long, TestCaseStepResult>();
  protected TestDeviceEntity testDeviceEntity;
  protected EnvironmentRunResult environmentRunResult;
  protected TestPlanRunSettingEntity testPlanRunSettingEntity;
  protected TestDeviceSettings testDeviceSettings;
  protected String testPlanId;
  protected WorkspaceType workspaceType;
  protected boolean skipExecution;
  protected String resultFailureMessage;
  private HttpClient httpClient;
  private int testCaseFetchWaitInterval;
  private int testCaseFetchMaxTries;


  public TestsuiteRunner() {
    this.testDeviceEntity = EnvironmentRunner.getRunnerEnvironmentEntity();
    this.environmentRunResult = EnvironmentRunner.getRunnerEnvironmentRunResult();
    this.testPlanRunSettingEntity = testDeviceEntity.getTestPlanSettings();
    this.testDeviceSettings = testDeviceEntity.getEnvSettings();
    this.testPlanId = EnvironmentRunner.getRunnerExecutionId();
    this.workspaceType = testDeviceEntity.getWorkspaceType();
    this.httpClient = EnvironmentRunner.getWebAppHttpClient();
    this.skipExecution = false;
    this.resultFailureMessage = null;
    this.testCaseFetchWaitInterval = AutomatorConfig.getInstance().getTestCaseFetchWaitInterval();
    this.testCaseFetchMaxTries = AutomatorConfig.getInstance().getTestCaseDefaultMaxTries();
  }

  public EnvironmentRunResult runSuites(List<TestSuiteEntity> testCaseGroupEntities) throws AutomatorException {
    log.debug("----- Running Test Suites For Environment Run Result [" + environmentRunResult.getId() + "] -----");
    List<TestSuiteResult> testCaseGroupsResults = new ArrayList<TestSuiteResult>();
    environmentRunResult.setGroupResults(testCaseGroupsResults);
    ResultConstant result = ResultConstant.SUCCESS;

    for (TestSuiteEntity testSuiteEntity : testCaseGroupEntities) {
      skipExecution = false;
      resultFailureMessage = null;
      TestSuiteResult testSuiteResult = new TestSuiteResult();
      testSuiteResult.setId(testSuiteEntity.getResultId());
      testSuiteResult.setGroupId(testSuiteEntity.getId());
      testSuiteResult.setEnvRunId(testSuiteEntity.getEnvironmentResultId());
      testSuiteResult.setExecutionInitiatedOn(environmentRunResult.getExecutionInitiatedOn());
      testSuiteResult.setAgentPickedOn(environmentRunResult.getAgentPickedOn());
      testSuiteResult.setDeviceAllocatedOn(environmentRunResult.getDeviceAllocatedOn());
      testCaseGroupsResults.add(testSuiteResult);
      groupResultMap.put(testSuiteEntity.getId(), testSuiteResult);
      testSuiteResult.setStartTime(new Timestamp(System.currentTimeMillis()));

      try {
        log.debug("Running Test Suite -  " + testSuiteEntity);

        try {
          checkSuitePrerequisiteFailure(testSuiteEntity, testSuiteResult);
          if (ExecutionEnvironmentRunner.isRunning()) {
            log.debug("Execution environment status is running...Proceeding with the test suite execution....");
            runSuite(testSuiteEntity, testSuiteResult);
          } else {
            log.debug("Execution environment status is stopped...stopping test suite execution....");
            testSuiteResult.setResult(ResultConstant.STOPPED);
            testSuiteResult.setMessage(AutomatorMessages.MSG_USER_ABORTED_EXECUTION);
            testSuiteResult.setEndTime(new Timestamp(System.currentTimeMillis()));
            postSuiteResult(testSuiteResult);
            break;
          }
        } catch (Exception ex) {
          log.error(ex.getMessage(), ex);
          testSuiteResult.setResult(ResultConstant.FAILURE);
          testSuiteResult.setMessage(ex.getMessage());
        }
        testSuiteResult.setEndTime(new Timestamp(System.currentTimeMillis()));
        postSuiteResult(testSuiteResult);
      } catch (Exception ex) {
        log.error("Unhandled exception while processing test suite");
        log.error(ex.getMessage(), ex);
        testSuiteResult.setEndTime(new Timestamp(System.currentTimeMillis()));
        testSuiteResult.setResult(ResultConstant.FAILURE);
        testSuiteResult.setMessage(ex.getMessage());
        try {
          postSuiteResult(testSuiteResult);
        } catch (Exception e) {
          log.error("Unhandled exception while sending test suite results");
          log.error(e.getMessage(), e);
        }
      }

      if (testSuiteResult.getResult().getId() > result.getId()) {
        result = testSuiteResult.getResult();
      }
    }

    if (environmentRunResult.getResult() == null) {
      environmentRunResult.setResult(result);
    }

    return environmentRunResult;
  }

  public abstract void startSession(Long entityId, DriverSessionType driverSessionType) throws AutomatorException;

  public void endSession() throws AutomatorException {
    DriverManager driverManager = DriverManager.getDriverManager();
    if (driverManager != null) {
      driverManager.endSession();
    }
  }

  private void populateThreadContextData(TestSuiteEntity testSuiteEntity
    , TestSuiteResult testSuiteResult) {
    ThreadContext.put("TEST_SUITE", testSuiteEntity.getId() + "");
    ThreadContext.put("TEST_SUITE_RESULT", testSuiteResult.getId() + "");
  }

  private void resetThreadContextData() {
    ThreadContext.put("TEST_SUITE", "");
    ThreadContext.put("TEST_SUITE_RESULT", "");
  }

  private void runSuite(TestSuiteEntity testSuiteEntity, TestSuiteResult testSuiteResult) throws AutomatorException {
    resetThreadContextData();
    populateThreadContextData(testSuiteEntity, testSuiteResult);
    log.debug("Running test suite - " + testSuiteEntity.getName());
    if (!testDeviceEntity.getCreateSessionAtCaseLevel()) {
      restartCurrentSession(testSuiteResult);
    }
    List<TestCaseEntity> testCaseEntityList = testSuiteEntity.getTestCases();
    List<TestCaseResult> testCasesResult = new ArrayList<>();
    testSuiteResult.setTestCaseResults(testCasesResult);
    testSuiteResult.setResult(ResultConstant.SUCCESS);
    boolean executionStarted = false;

    for (TestCaseEntity testCaseEntity : testCaseEntityList) {
      boolean testCaseRunFailed = false;
      boolean testCasePrerequisiteFailed = false;
      TestCaseResult testCaseResult = new TestCaseResult(testCaseEntity.getId());

      try {
        testCaseResult.setId(testCaseEntity.getTestCaseResultId());
        testCaseResult.setEnvRunId(testSuiteEntity.getEnvironmentResultId());
        testCaseResult.setGroupResultId(testSuiteEntity.getResultId());
        testCaseResult.setGroupId(testSuiteEntity.getId());
        testCaseResult.setTestCaseId(testCaseEntity.getId());
        testCaseResult.setTestDataSetName(testCaseEntity.getTestDataSetName());
        testCaseResult.setTestDataId(testCaseEntity.getTestDataId());
        testCaseResult.setIsStepGroup(testCaseEntity.getIsStepGroup());
        testCaseResult.setDataDriven(testCaseEntity.getIsDataDriven());
        testCaseResult.setStartTime(new Timestamp(System.currentTimeMillis()));
        testCasesResult.add(testCaseResult);

        testcaseResultMap.put(testCaseEntity.getId(), testCaseResult);

        if (skipExecution) {
          testCaseResult.setMessage(resultFailureMessage);
        } else if (hasPreRequisite(testCaseEntity)) {
          testCasePrerequisiteFailed = checkTestCasePrerequisiteFailure(testCaseEntity, testCaseResult);
        }

        try {
          if (!testCaseEntity.getIsDataDriven()) {
            testCaseEntity = getTestCase(testCaseEntity, this.testCaseFetchMaxTries);
            new ErrorUtil().checkError(testCaseEntity.getErrorCode(), testCaseEntity.getMessage());
          }
        } catch (TestsigmaNoParallelRunException e) {
          log.error(e.getMessage(), e);
          testCaseRunFailed = true;
          resultFailureMessage = e.getMessage();
          testCaseResult.setResult(ResultConstant.STOPPED);
          testCaseResult.setMessage(resultFailureMessage);
        } catch (AutomatorException e) {
          log.error(e.getMessage(), e);
          testCaseRunFailed = true;
          resultFailureMessage = e.getMessage();
          testCaseResult.setResult(ResultConstant.FAILURE);
          testCaseResult.setMessage(resultFailureMessage);
        }

        if (!testCaseRunFailed) {
          if (ExecutionEnvironmentRunner.isRunning()) {
            testSuiteResult.setSessionCreatedOn(new Timestamp(System.currentTimeMillis()));
            if (testCaseEntity.getIsDataDriven()) {
              runDataDrivenTestCase(testCaseEntity, testCaseResult, false, testCasePrerequisiteFailed);
            } else {
              new TestcaseRunner(testCaseEntity, testCaseResult, mapStepResult,
                skipExecution || testCasePrerequisiteFailed, resultFailureMessage)
                .run();
            }
            executionStarted = true;
          } else {
            testCaseResult.setResult(ResultConstant.STOPPED);
            testCaseResult.setMessage(AutomatorMessages.MSG_USER_ABORTED_EXECUTION);
            testCaseResult.setEndTime(new Timestamp(System.currentTimeMillis()));
            postTestcaseResult(testCaseResult);
            break;
          }
        }

        testCaseResult.setEndTime(new Timestamp(System.currentTimeMillis()));
        postTestcaseResult(testCaseResult);
      } catch (Exception ex) {
        log.error("Unhandled exception while processing test case");
        log.error(ex.getMessage(), ex);
        testCaseResult.setResult(ResultConstant.ABORTED);
        testCaseResult.setMessage(ex.getMessage());
        try {
          postTestcaseResult(testCaseResult);
        } catch (Exception e) {
          log.error("Unhandled exception while posting test case results");
          log.error(e.getMessage(), e);
        }
      }

      if (testCaseResult.getResult().getId() > testSuiteResult.getResult().getId()) {
        testSuiteResult.setResult(testCaseResult.getResult());
      }
    }

    testSuiteResult.setSessionCompletedOn(new Timestamp(System.currentTimeMillis()));
    testSuiteResult.setEndTime(new Timestamp(System.currentTimeMillis()));
    if (testSuiteResult.getResult() == ResultConstant.SUCCESS) {
      testSuiteResult.setMessage(AutomatorMessages.MSG_GROUP_SUCCESS);
    } else if (StringUtils.isBlank(testSuiteResult.getMessage())) {
      testSuiteResult.setMessage(AutomatorMessages.MSG_GROUP_FAILED);
    }
    resetThreadContextData();
  }

  private void restartCurrentSession(TestSuiteResult testSuiteResult) {

    if (workspaceType.equals(WorkspaceType.Rest)) {
      return;
    }

    DriverManager driverManager = DriverManager.getDriverManager();
    if (driverManager.isRestart() && (driverManager.getRestartSessionId() != null)) {
      try {
        log.info("Found that driver session restarted while executing a test suite. Storing session ID " +
          "in test suite result tables. Test Suite Result - " + testSuiteResult.getId());
        driverManager.storeSessionId(DriverSessionType.TEST_SUITE_SESSION, testSuiteResult.getId());
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  public void runDataDrivenTestCase(TestCaseEntity testCaseEntity, TestCaseResult testCaseResult,
                                    boolean testCaseRunFailed, boolean testCasePrerequisiteFailed) throws Exception {
    ResultConstant dataDrivenStatus = ResultConstant.SUCCESS;

    for (TestCaseEntity dataDrivenTestCase : testCaseEntity.getDataDrivenTestCases()) {
      TestCaseResult dataDrivenTestCaseResult = new TestCaseResult(dataDrivenTestCase.getId());

      dataDrivenTestCaseResult.setId(getResultId(testCaseEntity, dataDrivenTestCase.getTestDataSetName()));
      dataDrivenTestCaseResult.setGroupId(testCaseResult.getGroupId());
      dataDrivenTestCaseResult.setEnvRunId(environmentRunResult.getId());
      dataDrivenTestCaseResult.setGroupResultId(testCaseResult.getGroupResultId());
      dataDrivenTestCaseResult.setParentId(testCaseResult.getId());
      dataDrivenTestCaseResult.setTestDataSetName(dataDrivenTestCase.getTestDataSetName());
      dataDrivenTestCaseResult.setTestDataId(testCaseEntity.getTestDataId());
      dataDrivenTestCaseResult.setStartTime(new Timestamp(System.currentTimeMillis()));

      testCaseResult.getTestCaseResults().add(dataDrivenTestCaseResult);

      try {
        dataDrivenTestCase = getTestCase(dataDrivenTestCase, this.testCaseFetchMaxTries);
        new ErrorUtil().checkError(dataDrivenTestCase.getErrorCode(), dataDrivenTestCase.getMessage());
      } catch (AutomatorException e) {
        log.error(e.getMessage(), e);
        if (!(skipExecution || testCasePrerequisiteFailed)) {
          testCaseRunFailed = true;
          resultFailureMessage = e.getMessage();
          dataDrivenTestCaseResult.setResult(ResultConstant.FAILURE);
          dataDrivenTestCaseResult.setMessage(resultFailureMessage);
        }
      }

      if (!(testCaseRunFailed || testCasePrerequisiteFailed)) {
        if (ExecutionEnvironmentRunner.isRunning()) {
          new TestcaseRunner(dataDrivenTestCase, dataDrivenTestCaseResult, mapStepResult,
            skipExecution || testCasePrerequisiteFailed, resultFailureMessage).run();

          boolean isFailed = (ResultConstant.SUCCESS != dataDrivenTestCaseResult.getResult());

          if (skipExecution) {
            dataDrivenTestCaseResult.setResult(testCaseResult.getResult());
            dataDrivenTestCaseResult.setMessage(testCaseResult.getMessage());
          } else if (isFailed == dataDrivenTestCase.getExpectedToFail()) {
            dataDrivenTestCaseResult.setResult(ResultConstant.SUCCESS);
          } else {
            dataDrivenTestCaseResult.setResult(ResultConstant.FAILURE);
          }
        } else {
          dataDrivenTestCaseResult.setResult(ResultConstant.STOPPED);
          dataDrivenTestCaseResult.setMessage(AutomatorMessages.MSG_USER_ABORTED_EXECUTION);
          dataDrivenTestCaseResult.setEndTime(new Timestamp(System.currentTimeMillis()));
          postTestcaseResult(dataDrivenTestCaseResult);
          break;
        }
      } else if (testCasePrerequisiteFailed) {
        dataDrivenTestCaseResult.setResult(testCaseResult.getResult());
        dataDrivenTestCaseResult.setMessage(testCaseResult.getMessage());
      }

      dataDrivenStatus = (dataDrivenTestCaseResult.getResult().getId() > dataDrivenStatus.getId()) ?
        dataDrivenTestCaseResult.getResult() : dataDrivenStatus;
      dataDrivenTestCaseResult.setEndTime(ObjectUtils.defaultIfNull(dataDrivenTestCaseResult.getEndTime(),
        new Timestamp(System.currentTimeMillis())));
      postTestcaseResult(dataDrivenTestCaseResult);
    }
    testCaseResult.setResult(ObjectUtils.defaultIfNull(testCaseResult.getResult(), dataDrivenStatus));

    if (testCaseResult.getResult() == ResultConstant.SUCCESS) {
      testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_SUCCESS);
    } else if (StringUtils.isBlank(testCaseResult.getMessage())) {
      testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_FAILURE);
    }
  }


  private TestCaseEntity getTestCase(TestCaseEntity testCaseEntity, int maxTries) throws AutomatorException {
    try {
      TestCaseEntity testCaseEntityCopy = testCaseEntity;
      testCaseEntity = AutomatorConfig.getInstance().getAppBridge().getTestCase(environmentRunResult.getId(),
        testCaseEntity);

      if (testCaseEntity != null) {
        if (ResultConstant.STOPPED == testCaseEntity.getResult()) {
          ExecutionEnvironmentRunner.setStoppedStatus();
        }
      }
      if ((testCaseEntity.getErrorCode() != null) && maxTries > 0) {
        RemoteWebDriver remoteWebDriver = DriverManager.getDriverManager().getDriver().getRemoteWebDriver();
        remoteWebDriver.getWindowHandle();
        Thread.sleep(this.testCaseFetchWaitInterval);
        return getTestCase(testCaseEntityCopy, maxTries - 1);
      }

      return testCaseEntity;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new AutomatorException(ErrorCodes.TEST_CASE_DETAILS_FETCH_FAILED,
        AutomatorMessages.FAILED_TO_FETCH_TEST_CASE_DETAILS + " - " + e.getMessage());
    }
  }

  private Long getResultId(TestCaseEntity entityList, String iteration) {
    for (TestCaseEntity entity : entityList.getDataDrivenTestCases()) {
      if (entity.getTestDataSetName() != null && entity.getTestDataSetName().equals(iteration)) {
        return entity.getTestCaseResultId();
      }
    }
    return null;
  }

  public void postTestcaseResult(TestCaseResult testCaseResult) throws Exception {
    AutomatorConfig.getInstance().getAppBridge().postTestCaseResult(testCaseResult);
  }

  public void postSuiteResult(TestSuiteResult testSuiteResult) throws Exception {
    AutomatorConfig.getInstance().getAppBridge().postTestSuiteResult(testSuiteResult);
  }

  private boolean hasPreRequisite(TestSuiteEntity testSuiteEntity) {
    boolean hasPreRequisite = false;
    if (testSuiteEntity.getPreRequisite() != null) {
      if (testSuiteEntity.getPreRequisite() > 0) {
        hasPreRequisite = true;
      } else if (testSuiteEntity.getPreRequisite() == 0) {
        log.error("Test Case Group entity found with 0 as value for prerequisite");
      }
    }
    return hasPreRequisite;
  }

  private boolean hasPreRequisite(TestCaseEntity testCaseEntity) {
    boolean hasPreRequisite = false;
    if (testCaseEntity.getPreRequisite() != null) {
      if (testCaseEntity.getPreRequisite() > 0) {
        hasPreRequisite = true;
      } else if (testCaseEntity.getPreRequisite() == 0) {
        log.error("Test Case entity found with 0 as value for prerequisite");
      }
    }
    return hasPreRequisite;
  }

  private void checkSuitePrerequisiteFailure(TestSuiteEntity testSuiteEntity,
                                             TestSuiteResult testSuiteResult) {
    boolean hasPrerequisite = hasPreRequisite(testSuiteEntity);

    if (hasPrerequisite) {
      TestSuiteResult prerequisiteTestSuiteResult = groupResultMap.get(testSuiteEntity.getPreRequisite());
      log.debug("Found a prerequisite for the test suite. Checking its result - " + prerequisiteTestSuiteResult);
      boolean prerequisiteFailed = ((prerequisiteTestSuiteResult == null)
        || (ResultConstant.SUCCESS != prerequisiteTestSuiteResult.getResult()));

      if (prerequisiteFailed) {
        testSuiteResult.setResult(ResultConstant.FAILURE);
        testSuiteResult.setMessage(AutomatorMessages.MSG_GROUP_PRE_REQUISITE_FAILED);

        if (testPlanRunSettingEntity.getOnSuitePreRequisiteFail() == PreRequisiteAction.Abort) {
          testSuiteResult.setEndTime(new Timestamp(System.currentTimeMillis()));
          skipExecution = true;
          resultFailureMessage = AutomatorMessages.MSG_GROUP_PRE_REQUISITE_FAILED;
        }
      }
    } else {
      log.debug("Test has no prerequisites. Proceeding with normal execution...");
    }
  }

  private boolean checkTestCasePrerequisiteFailure(TestCaseEntity testCaseEntity, TestCaseResult testCaseResult) {
    boolean testCasePrerequisiteFailed = false;

    TestCaseResult prerequisiteTestCaseResult = testcaseResultMap.get(testCaseEntity.getPreRequisite());
    log.debug("Found that test case has prerequisite. Checking the prerequisite result - " + prerequisiteTestCaseResult);

    boolean abortOnPrerequisiteFailure = (((prerequisiteTestCaseResult == null)
      || (prerequisiteTestCaseResult.getResult() != ResultConstant.SUCCESS))
      && (testPlanRunSettingEntity.getOnTestcasePreRequisiteFail() == PreRequisiteAction.Abort));

    if (abortOnPrerequisiteFailure) {
      log.debug("Prerequisite failed for test suite. Aborting....");
      testCaseResult.setResult(ResultConstant.FAILURE);
      testCaseResult.setMessage(AutomatorMessages.MSG_CASE_PRE_REQUISITE_FAILED);
      testCaseResult.setEndTime(new Timestamp(System.currentTimeMillis()));
      testCasePrerequisiteFailed = true;
      resultFailureMessage = AutomatorMessages.MSG_CASE_PRE_REQUISITE_FAILED;
    }
    return testCasePrerequisiteFailed;
  }
}
