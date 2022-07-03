package com.testsigma.automator.runners;

import com.testsigma.automator.AutomatorConfig;
import com.testsigma.automator.constants.DriverSessionType;
import com.testsigma.automator.constants.ErrorCodes;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.service.ObjectMapperService;
import com.testsigma.automator.utilities.ScreenCaptureUtil;
import com.testsigma.automator.utilities.ScreenshotUploadTask;
import com.testsigma.automator.utilities.UploadThreadPool;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import java.sql.Timestamp;
import java.util.*;

@Log4j2
public class TestcaseRunner {

  private static final String[] SKIP_GETURL = {"Close all windows", "Verify that the Alert is not present",
    "Verify that the Alert displays the message", "Click on Cancel button in the alert",
    "Click OK button in the alert", "Verify that an Alert with text", "Wait until an Alert is displayed in the current page", "Wait until the Alert currently displayed is absent"};
  public final int SAVE_BATCH_IMAGES = 10;
  protected TestDeviceEntity testDeviceEntity;
  protected EnvironmentRunResult environmentRunResult;
  protected TestPlanRunSettingEntity testPlanRunSettingEntity;
  protected TestDeviceSettings testDeviceSettings;
  protected String testPlanId;
  protected WorkspaceType workspaceType;
  protected TestCaseEntity testCaseEntity;
  protected TestCaseResult testCaseResult;
  protected Map<Long, TestCaseStepResult> mapStepResult;
  protected boolean skipExecution;
  protected String resultFailureMessage;

  public TestcaseRunner(TestCaseEntity testCaseEntity, TestCaseResult testCaseResult,
                        Map<Long, TestCaseStepResult> mapStepResult, boolean skipExecution,
                        String resultFailureMessage) {
    this.testDeviceEntity = EnvironmentRunner.getRunnerEnvironmentEntity();
    this.environmentRunResult = EnvironmentRunner.getRunnerEnvironmentRunResult();
    this.testPlanRunSettingEntity = testDeviceEntity.getTestPlanSettings();
    this.testDeviceSettings = testDeviceEntity.getEnvSettings();
    this.testPlanId = EnvironmentRunner.getRunnerExecutionId();
    this.workspaceType = testDeviceEntity.getWorkspaceType();
    this.testCaseEntity = testCaseEntity;
    this.testCaseResult = testCaseResult;
    this.mapStepResult = mapStepResult;
    this.skipExecution = skipExecution;
    this.resultFailureMessage = resultFailureMessage;
  }

  public void startSession() throws AutomatorException {
    DriverManager.getDriverManager(testDeviceEntity, testDeviceEntity.getWorkspaceType(), testDeviceSettings.getOs(),
      testCaseResult.getId(), DriverSessionType.TEST_CASE_SESSION);
  }

  public void endSession() throws AutomatorException {
    DriverManager driverManager = DriverManager.getDriverManager();
    if (driverManager != null) {
      driverManager.endSession();
    }
  }

  private void populateThreadContextData() {
    ThreadContext.put("TEST_CASE", testCaseEntity.getId() + "");
    ThreadContext.put("TEST_CASE_RESULT", testCaseResult.getId() + "");
  }

  private void resetThreadContextData() {
    ThreadContext.put("TEST_CASE", "");
    ThreadContext.put("TEST_CASE_RESULT", "");
  }

  public TestCaseResult run() throws Exception {
    resetThreadContextData();
    populateThreadContextData();
    log.info("Running testcase - " + testCaseEntity.getTestCaseName());
    if (testDeviceEntity.getCreateSessionAtCaseLevel()) {
      startSession();
    } else {
      restartCurrentSession();
    }
    environmentRunResult.setSessionCreatedOn(new Timestamp(System.currentTimeMillis()));
    ResultConstant result = ResultConstant.SUCCESS;
    List<TestCaseStepEntity> stepList = testCaseEntity.getTestSteps();
    List<TestCaseStepResult> testCaseStepsResult = new ArrayList<>();
    testCaseResult.setTestCaseStepResults(testCaseStepsResult);
    testCaseResult.setIsStepGroup(testCaseEntity.getIsStepGroup());
    testCaseResult.setDataDriven(testCaseEntity.getIsDataDriven());
    testCaseResult.setTestPlanResultId(testDeviceEntity.getExecutionRunId());
    testCaseResult.setTestCaseName(testCaseEntity.getTestCaseName());
    try {
      HashMap<Long, TestCaseStepResult> parentStatus = new HashMap<>();
      int currentIndex = 0;
      testCaseResult.setCurrentIndex(currentIndex);
      int lastStep = stepList.size() - 1;
      ScreenCaptureUtil screenCaptureUtil = new ScreenCaptureUtil(new ArrayList<>());
      for (int i = 0; i < stepList.size(); i++) {
        TestCaseStepEntity testCaseStepEntity = stepList.get(i);
        TestCaseStepResult testCaseStepResult = new TestCaseStepResult();
        testCaseStepResult.setTestCaseStepId(testCaseStepEntity.getId());
        testCaseStepResult.setSkipExe(skipExecution);
        testCaseStepResult.setSkipMessage(resultFailureMessage);
      /*  if (!skipExecution && (workspaceType.equals(WorkspaceType.WebApplication) || workspaceType
          .equals(WorkspaceType.MobileWeb))) {
          url = getUrl();
        }*/
        boolean skipGetUrl = false;
        if (testCaseStepEntity.getAction() != null) {
          skipGetUrl = Arrays.asList(SKIP_GETURL).stream().filter(action -> testCaseStepEntity.getAction().contains(action)).count() > 0;
        }

        TestStepType type = testCaseStepEntity.getType();
        TestcaseStepRunner testcaseStepRunner =
          new TestcaseStepRunnerFactory().getRunner(this.workspaceType, testDeviceSettings.getOs(), type);
        TestCaseStepResult parentResult = parentStatus.get(testCaseStepEntity.getParentId());
        RunnerUtil util = new RunnerUtil();
        boolean isFailure =
          util.canSkipNormalStep(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.nestedConditionalStep(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.canSkipIfElse(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.canSkipIfElseIf(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.canSkipElseIfElseIf(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.canSkipElseIfElse(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.canSkipIfCondition(parentResult, testCaseStepEntity, testCaseStepResult)
            || util.canSkipForLoop(parentResult, testCaseStepEntity, testCaseStepResult);

        log.info(new ObjectMapperService().convertToJson(testCaseStepEntity));

        int stepResultUpdateSize = 10;

        if (!skipExecution && isFailure) {
          parentStatus.put(testCaseStepEntity.getId(), testCaseStepResult);
          testCaseStepResult.setResult(ResultConstant.NOT_EXECUTED);
          int processedSteps = processedStepCount(testCaseStepsResult);

          if (processedSteps % stepResultUpdateSize == 0) {
            testCaseResult.setTestCaseStepResults(testCaseStepsResult);
            postTestcaseResult();
            currentIndex = +stepResultUpdateSize;
            processedSteps = 0;
            testCaseStepsResult = new ArrayList<>();
            testCaseResult.setCurrentIndex(currentIndex);

          }

          if (screenCaptureUtil.screenshots.size() > 0 && (lastStep == i)) {
            log.debug("Screenshots task for :::" + testCaseStepResult);
            final String requestId = ThreadContext.get("X-Request-Id");
            UploadThreadPool.getInstance().upload(new ScreenshotUploadTask(screenCaptureUtil.screenshots, requestId, testDeviceEntity));
            screenCaptureUtil.screenshots = new ArrayList<>();
          }

          testCaseStepResult = testcaseStepRunner
            .run(testCaseStepEntity, testCaseStepResult, mapStepResult, testCaseResult, parentStatus,
              false, false, screenCaptureUtil);
          testCaseStepsResult.add(testCaseStepResult);
          mapStepResult.put(testCaseStepEntity.getId(), testCaseStepResult);
          continue;
        }

        testCaseStepResult = testcaseStepRunner
          .run(testCaseStepEntity, testCaseStepResult, mapStepResult, testCaseResult, parentStatus,
            false, false, screenCaptureUtil);
        if (screenCaptureUtil.screenshots.size() == SAVE_BATCH_IMAGES || (lastStep == i)) {
          final String requestId = ThreadContext.get("X-Request-Id");
          UploadThreadPool.getInstance().upload(new ScreenshotUploadTask(screenCaptureUtil.screenshots, requestId, testDeviceEntity));
          screenCaptureUtil.screenshots = new ArrayList<>();
        }
        skipExecution = testCaseStepResult.getSkipExe();
        resultFailureMessage = testCaseStepResult.getSkipMessage();

        testCaseStepsResult.add(testCaseStepResult);
        mapStepResult.put(testCaseStepEntity.getId(), testCaseStepResult);
        TestCaseStepEntity stepGroup = null;
        TestCaseStepResult stepGroupResult = null;
        if (testCaseStepEntity.getStepGroupId() != null) {
          stepGroup = ((testCaseStepEntity.getTestCaseSteps() != null) && (testCaseStepEntity.getTestCaseSteps().size() > 0)) ?
            testCaseStepEntity.getTestCaseSteps().get(0) : null;
          stepGroupResult = ((testCaseStepResult.getStepResults() != null) && (testCaseStepResult.getStepResults().size() > 0)) ?
            testCaseStepResult.getStepResults().get(0) : null;
        }
        if (i == 0 && ((workspaceType == WorkspaceType.WebApplication) || (workspaceType
          == WorkspaceType.MobileWeb)) && (getUrl() != null) && !skipGetUrl && ((testCaseStepEntity.getStepGroupId() != null && stepGroup != null
          && (stepGroup.getStepGroupId() != null && !stepGroup.getAction().contains(AutomatorMessages.KEYWORD_GO_TO) && stepGroupResult
          .getResult() == ResultConstant.SUCCESS)) ||
          (testCaseStepEntity.getAction() != null && !testCaseStepEntity.getAction()
            .contains(AutomatorMessages.KEYWORD_GO_TO) && testCaseStepResult.getResult() == ResultConstant.SUCCESS))
        ) {
        } else if (i == (stepList.size() - 1) && !skipGetUrl && testCaseStepResult.getResult() == ResultConstant.SUCCESS
          && (workspaceType.equals(WorkspaceType.WebApplication) || workspaceType.equals(WorkspaceType.MobileWeb))) {
          ExecutionEnvironmentRunner.addUrl(testPlanId, testCaseEntity.getId(), getUrl());
        }

        //TODO:use check based step type
        if ((testCaseStepEntity.getConditionType() == null || testCaseStepEntity.getConditionType() == ConditionType.NOT_USED
            || ConditionType.LOOP_FOR == testCaseStepEntity.getConditionType()) && (testCaseStepEntity.getStepDetails().getIgnoreStepResult() != null && !testCaseStepEntity.getStepDetails().getIgnoreStepResult()) ) {
          result = (result.getId() < testCaseStepResult.getResult().getId()) ? testCaseStepResult.getResult() : result;
        }
        int processedSteps = processedStepCount(testCaseStepsResult);
        if (processedSteps % stepResultUpdateSize == 0) {
          testCaseResult.setTestCaseStepResults(testCaseStepsResult);
          postTestcaseResult();
          currentIndex = +stepResultUpdateSize;
          processedSteps = 0;
          testCaseStepsResult = new ArrayList<>();
          testCaseResult.setCurrentIndex(currentIndex);
        }

      }
      testCaseResult.setTestCaseStepResults(testCaseStepsResult);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    testCaseResult.setResult(ObjectUtils.defaultIfNull(testCaseResult.getResult(), result));
    testCaseResult.setEndTime(new Timestamp(System.currentTimeMillis()));

    if ((testCaseResult.getResult() == ResultConstant.SUCCESS) && (result == ResultConstant.SUCCESS)) {
      testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_SUCCESS);
    } else if (StringUtils.isBlank(testCaseResult.getMessage())) {
      testCaseResult.setMessage(AutomatorMessages.MSG_TEST_CASE_FAILURE);
    }

    // check for browser closed condition
    if (testCaseResult.getResult() != ResultConstant.SUCCESS) {
      if (ErrorCodes.BROWSER_CLOSED.equals(testCaseResult.getErrorCode())) {
        DriverManager.getDriverManager().setRestartDriverSession(Boolean.TRUE);
      }
    }

    if (testDeviceEntity.getCreateSessionAtCaseLevel()) {
      endSession();
    }
    resetThreadContextData();
    return testCaseResult;
  }

  private boolean startNewDriverSession() {
    boolean shouldStart = false;
    DriverManager driverManager = DriverManager.getDriverManager();
    String capabilityStr = hasDriverSession();
    String currentSessionId = DriverManager.getDriverManager().getDriver().getRemoteWebDriver().getSessionId().toString();
    if (driverManager != null) {
      shouldStart = driverManager.getRestartDriverSession() || currentSessionId == null
        || (currentSessionId != null && capabilityStr == null);
    }
    return shouldStart;
  }

  public String getUrl() {
    try {
      return DriverManager.getDriverManager().getDriver().getRemoteWebDriver().getCurrentUrl();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public String hasDriverSession() {
    try {
      return DriverManager.getDriverManager().getDriver().getRemoteWebDriver().getCapabilities().toString();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  public void postTestcaseResult() throws Exception {
    AutomatorConfig.getInstance().getAppBridge().postTestCaseResult(testCaseResult);
  }

  protected int processedStepCount(List<TestCaseStepResult> testCaseStepResults) {
    int processedSteps = testCaseStepResults.size();
    for (TestCaseStepResult step : testCaseStepResults) {
      processedSteps += step.getStepResults().size();
    }
    return processedSteps;
  }

  private void restartCurrentSession() throws AutomatorException {
    if (workspaceType.equals(WorkspaceType.Rest)) {
      return;
    }

    DriverManager driverManager = DriverManager.getDriverManager();
    if (startNewDriverSession()) {
      log.info("Found startNewDriverSession flag to be true. Starting a new driver session.");
      driverManager.endSession();
      driverManager.startSession(DriverSessionType.TEST_CASE_SESSION, testCaseResult.getId(), Boolean.TRUE);
      driverManager.setRestartDriverSession(Boolean.FALSE);
    } else {
      log.info("Found startNewDriverSession flag to be false. Continuing with the existing driver session.");
      if (driverManager.isRestart()) {
        driverManager.storeSessionId(DriverSessionType.TEST_CASE_SESSION, testCaseResult.getId());
      }
    }
  }
}
