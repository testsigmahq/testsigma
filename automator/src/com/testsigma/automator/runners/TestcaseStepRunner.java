/*
 * ****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ***************************************************************************
 */

package com.testsigma.automator.runners;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.automator.constants.EnvSettingsConstants;
import com.testsigma.automator.constants.NaturalTextActionConstants;
import com.testsigma.automator.constants.AutomatorMessages;
import com.testsigma.automator.drivers.DriverManager;
import com.testsigma.automator.entity.*;
import com.testsigma.automator.exceptions.AutomatorException;
import com.testsigma.automator.service.AddonService;
import com.testsigma.automator.service.ObjectMapperService;
import com.testsigma.automator.utilities.RuntimeDataProvider;
import com.testsigma.automator.utilities.ScreenCaptureUtil;
import com.testsigma.sdk.TestData;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;

@Log4j2
public abstract class TestcaseStepRunner {
  private static final String[] SKIP_SCREENSHOT_KEYWORDS = {"Close App", "Close all windows"};
  public static final String INVALID_SESSION = "The browser connection is lost. Either the browser is closed by the user or the connection is terminated.";
  public static final String BROWSER_UNREACHABLE = "The browser connection is lost. Either the browser is closed by the user or the connection is terminated. Session will be reset. ";
  protected String testPlanId;
  protected WorkspaceType workspaceType;
  protected Platform os;
  private ObjectMapperService objectMapperService;
  private AddonService addonService;

  public TestcaseStepRunner() {
    this.objectMapperService = new ObjectMapperService();
  }

  public TestcaseStepRunner(WorkspaceType workspaceType, Platform os) {
    this.testPlanId = EnvironmentRunner.getRunnerExecutionId();
    this.workspaceType = workspaceType;
    this.os = os;
    this.objectMapperService = new ObjectMapperService();
    this.addonService = AddonService.getInstance();
  }

  public Platform getOs() {
    return os;
  }

  private void populateThreadContextData(TestCaseStepEntity testCaseStepEntity) {
    ThreadContext.put("TEST_STEP", testCaseStepEntity.getId() + "");
  }

  private void resetThreadContextData() {
    ThreadContext.put("TEST_STEP", "");
  }

  public TestCaseStepResult run(TestCaseStepEntity testCaseStepEntity, TestCaseStepResult testCaseStepResult,
                                Map<Long, TestCaseStepResult> testCaseStepResultMap, TestCaseResult testCaseResult,
                                HashMap<Long, TestCaseStepResult> parentStatus, boolean failedToProcess,
                                boolean isGroupStep, ScreenCaptureUtil screenCaptureUtil) {
    resetThreadContextData();
    populateThreadContextData(testCaseStepEntity);
    log.info("Executing step - " + testCaseStepEntity.getId());
    //TODO: check test data type and update value for run time data.
    //For all other types data ll be updated on server side
    ExecutionLabType executionLabType = EnvironmentRunner.getRunnerEnvironmentEntity().getExecutionLabType();
    TestPlanRunSettingEntity testPlanRunSettingEntity = EnvironmentRunner.getRunnerEnvironmentEntity().getTestPlanSettings();
    Long envRunId = EnvironmentRunner.getRunnerEnvironmentRunResult().getId();
    TestDeviceSettings testDeviceSettings = EnvironmentRunner.getRunnerEnvironmentEntity().getEnvSettings();

    testCaseStepResult.setEnvRunId(envRunId);
    testCaseStepResult.setTestCaseResultId(testCaseResult.getId());
    testCaseStepResult.setStartTime(new Timestamp(System.currentTimeMillis()));
    testCaseStepResult.setTestCaseId(testCaseStepEntity.getTestCaseId());
    testCaseStepResult.setStepGroupId(testCaseStepEntity.getStepGroupId());
    testCaseStepResult.setParentId(testCaseStepEntity.getParentId());
    testCaseStepResult.setConditionType(testCaseStepEntity.getConditionType());
    testCaseStepResult.setTestCaseStepType(testCaseStepEntity.getType());
    testCaseStepResult.setStepDetails(testCaseStepEntity.getStepDetails());
    testCaseStepResult.setAddonTestData(testCaseStepEntity.getAddonTestData());
    testCaseStepResult.setAddonElements(testCaseStepEntity.getAddonElements());
    testCaseStepResult.setWaitTime(testCaseStepEntity.getWaitTime());
    testCaseStepResult.setTestPlanRunSettingEntity(testPlanRunSettingEntity);
    testCaseStepResult.setPriority(testCaseStepEntity.getPriority());
    testCaseStepResult.setVisualEnabled(testCaseStepEntity.getVisualEnabled());
    testCaseStepResult.setForLoopCondition(testCaseStepEntity.getForLoopCondition());

    Map<String, String> envDetails = new HashMap<String, String>();
    envDetails.put("testcaseId", testCaseResult.getTestCaseId() + "");
    envDetails.put("iteration", testCaseResult.getTestDataSetName());
    envDetails.put("envRunId", envRunId + "");
    envDetails.put(EnvSettingsConstants.KEY_USERNAME, testDeviceSettings.getUserName());
    Integer pageTimeOut = testPlanRunSettingEntity.getPageTimeOut();
    if (pageTimeOut != null)
      envDetails.put(EnvSettingsConstants.PAGE_LOAD_TIMEOUT, testPlanRunSettingEntity.getPageTimeOut().toString());
    envDetails.put("jwtApiKey", testDeviceSettings.getJwtApiKey());
    if (testDeviceSettings.getEnvironmentParamId() != null) {
      envDetails.put(EnvSettingsConstants.KEY_ENVIRONMENT_PARAM_ID,
        testDeviceSettings.getEnvironmentParamId() + "");
    }
    ResultConstant status = ResultConstant.SUCCESS;

    if (testCaseStepEntity.getConditionType() != null &&
      (testCaseStepEntity.getConditionType() == ConditionType.CONDITION_ELSE) && !testCaseStepResult.getSkipExe()) {
      setConditionResult(testCaseStepEntity, testCaseStepResult, parentStatus);
      testCaseStepResult.setEndTime(new Timestamp(System.currentTimeMillis()));
      return testCaseStepResult;
    }
    try {
      TestCaseStepResult preReqResult = testCaseStepResultMap.get(testCaseStepEntity.getPreRequisite());
      boolean hasPreReq = (testCaseStepEntity.getPreRequisite() != null) && (testCaseStepEntity.getPreRequisite() > 0);

      if (hasPreReq) {
        log.debug("Test Step Prerequisite - " + testCaseStepEntity.getPreRequisite());
        log.debug("Test Step Prerequisite Result - " + objectMapperService.convertToJson(preReqResult));
      } else {
        log.debug("Test Step Has No Prerequisite");
      }

      boolean preReqFailed = hasPreReq && ((preReqResult == null) || (preReqResult.getResult() != ResultConstant.SUCCESS));

      boolean isRunning = ExecutionEnvironmentRunner.isRunning();

      boolean isStepGroup = (testCaseStepEntity.getStepGroupId() != null) && (testCaseStepEntity.getStepGroupId() > 0);

      if (!isRunning) {
        log.debug("Found execution environment / test case as stopped...skipping test case step execution");
        status = ResultConstant.STOPPED;
        testCaseStepResult.setMessage(AutomatorMessages.MSG_USER_ABORTED_EXECUTION);
      } else if(testCaseStepEntity.getFailureMessage() != null){
        testCaseStepResult.setResult(ResultConstant.FAILURE);
        status = ResultConstant.FAILURE;
        testCaseStepResult.setMessage(testCaseStepEntity.getFailureMessage());
      } if (testCaseStepResult.getSkipExe()) {
        log.debug("Found execution skip flag to be set...setting appropriate message");
        status = ResultConstant.NOT_EXECUTED;
        testCaseStepResult.setMessage(testCaseStepResult.getSkipMessage());
      } else if (failedToProcess) {
        log.debug("Found failed to process to be set...setting appropriate message");
        status = ResultConstant.NOT_EXECUTED;
        testCaseStepResult.setMessage(testCaseStepResult.getSkipMessage());
      } else if (preReqFailed) {
        log.debug("Found prerequisite failed flag to be set...setting appropriate message");
        status = ResultConstant.FAILURE;
        testCaseStepResult.setMessage(AutomatorMessages.MSG_STEP_PRE_REQUISITE_FAILED);
        boolean skipExe = (testPlanRunSettingEntity.getOnStepPreRequisiteFail() == RecoverAction.Run_Next_Testcase);
        if (skipExe) {
          testCaseResult.setResult(status);
          testCaseResult.setMessage(AutomatorMessages.MSG_STEP_PRE_REQUISITE_FAILED);
        }
        testCaseStepResult.setSkipExe(skipExe);
        testCaseStepResult.setSkipMessage(AutomatorMessages.MSG_STEP_PRE_REQUISITE_FAILED);
      }

      if ((testCaseStepEntity.getType() != null && testCaseStepEntity.getType() == TestStepType.FOR_LOOP) ||
              (testCaseStepEntity.getConditionType() != null && testCaseStepEntity.getConditionType() == ConditionType.LOOP_FOR)) {
        log.debug("For loop execution starts:" + testCaseStepEntity.getId() + " With Iteration:" + testCaseStepEntity.getIteration());
        status = executeForLoop(testCaseStepEntity, testCaseStepResult, testCaseStepResultMap, testCaseResult, parentStatus,
          failedToProcess, screenCaptureUtil, status);

      } else if (testCaseStepEntity.getType() != null && testCaseStepEntity.getType() == TestStepType.WHILE_LOOP) {
        log.debug("While loop execution starts:" + testCaseStepEntity.getId());
        status = executeWhileLoop(testCaseStepEntity, testCaseStepResult,
          testCaseStepResultMap, testCaseResult, parentStatus,
          failedToProcess, screenCaptureUtil, status);
      } else if (isStepGroup) {
        log.debug("Step type is Step Group. Executing Test Component with ID - " + testCaseStepEntity.getStepGroupId());
        status = executeStepGroup(testCaseStepEntity, testCaseStepResult, testCaseStepResultMap, testCaseResult, parentStatus,
          failedToProcess, screenCaptureUtil, status);
      } else if (isRunning && !testCaseStepResult.getSkipExe() && !preReqFailed && StringUtils.isEmpty(testCaseStepEntity.getFailureMessage())) {
        setTestDataValue(testCaseStepEntity, envDetails, testCaseResult, testCaseStepResult);
        testCaseStepResult.setElementDetails(testCaseStepEntity.getElementsMap());
        testCaseStepResult.setTestDataDetails(testCaseStepEntity.getTestDataMap());
        log.debug("Step type is normal. Executing normal Action step");
        execute(envDetails, testCaseStepResult, testCaseStepEntity, testCaseResult);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Map<String, Object> metadata = mapper.convertValue(testCaseStepEntity, Map.class);
        Map<String, Object> oldMetadata = mapper.convertValue(testCaseStepResult.getMetadata(), Map.class);
        oldMetadata.putAll(metadata);
        StepResultMetadataEntity metadataEntity = mapper.convertValue(oldMetadata, StepResultMetadataEntity.class);
        testCaseStepResult.setMetadata(metadataEntity);
        testCaseStepResult.getMetadata().setTestStep(testCaseStepEntity);
        takeScreenshot(workspaceType, testCaseStepEntity, testCaseStepResult,
          testPlanRunSettingEntity.getScreenshot(), screenCaptureUtil);

        if (testCaseStepEntity.getConditionType() != null && (testCaseStepEntity.getConditionType() != ConditionType.NOT_USED)) {
          setConditionResult(testCaseStepEntity, testCaseStepResult, parentStatus);
          status = (testCaseStepResult.getResult() != ResultConstant.SUCCESS) ? testCaseStepResult.getResult() : status;
        } else {
          status = (testCaseStepResult.getResult() != ResultConstant.SUCCESS) ? testCaseStepResult.getResult() : status;
          if ((status == ResultConstant.SUCCESS) && StringUtils.isBlank(testCaseStepResult.getMessage())) {
            testCaseStepResult.setMessage(AutomatorMessages.MSG_STEP_SUCCESS);
          } else if (StringUtils.isBlank(testCaseStepResult.getMessage())) {
            testCaseStepResult.setMessage(AutomatorMessages.MSG_STEP_FAILURE);
          }
        }

        log.debug("Test Step Result - " + status);
      }

      log.debug("Test Step Result [2] - " + status);

      boolean majorFailure = (status != ResultConstant.SUCCESS && status != ResultConstant.ABORTED) &&
        (testCaseStepEntity.getPriority() == TestStepPriority.MAJOR &&
          testPlanRunSettingEntity.getRecoveryAction() == RecoverAction.Run_Next_Testcase && testPlanRunSettingEntity.getOnStepPreRequisiteFail() !=RecoverAction.Run_Next_Step);
      boolean hasToAbortTestcase = (majorFailure && !isGroupStep && !isStepGroup);

      if (!testCaseStepResult.getSkipExe() && hasToAbortTestcase) {
        setFailedMessage(testCaseStepResult, testCaseResult, testCaseStepEntity.getStepDetails().getIgnoreStepResult());
      }
      //Add Loop level result
      if (testCaseStepEntity.getType() == TestStepType.BREAK_LOOP && status == ResultConstant.SUCCESS) {
        testCaseStepResult.setIsBreakLoop(true);
      } else if (testCaseStepEntity.getType() == TestStepType.CONTINUE_LOOP && status == ResultConstant.SUCCESS) {
        testCaseStepResult.setIsContinueLoop(true);
      }
    } catch (UnreachableBrowserException e) {
      status = ResultConstant.FAILURE;
      testCaseStepResult.setErrorCode(com.testsigma.automator.constants.ErrorCodes.BROWSER_CLOSED);
      testCaseStepResult.setMessage(String.format(BROWSER_UNREACHABLE));
    } catch (NoSuchSessionException e) {
      status = ResultConstant.FAILURE;
      testCaseStepResult.setMessage(String.format(INVALID_SESSION));

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      status = ResultConstant.FAILURE;
      testCaseStepResult.setErrorCode(com.testsigma.automator.constants.ErrorCodes.UNKNOWN_PROBLEM);
      if(testCaseStepResult.getMessage()==null)
        testCaseStepResult.setMessage(e.getMessage());
    }
    resetThreadContextData();
    testCaseStepResult.setResult(status);
    testCaseStepResult.setEndTime(new Timestamp(System.currentTimeMillis()));
    log.debug("Finished Executing Test Case Step - " + testCaseStepResult.getTestCaseStepId());
    return testCaseStepResult;
  }

  protected ResultConstant executeStepGroup(TestCaseStepEntity testCaseStepEntity, TestCaseStepResult testCaseStepResult,
                                            Map<Long, TestCaseStepResult> testCaseStepResultMap, TestCaseResult testCaseResult,
                                            HashMap<Long, TestCaseStepResult> parentStatus, boolean failedToProcess,
                                            ScreenCaptureUtil screenCaptureUtil, ResultConstant currentStatus) throws Exception {
    executeGroup(testCaseStepEntity, testCaseStepResult, testCaseStepResultMap, testCaseResult, parentStatus, failedToProcess, screenCaptureUtil);
    if (testCaseStepEntity.getConditionType() != null && (testCaseStepEntity.getConditionType() != ConditionType.NOT_USED)) {
      setConditionResult(testCaseStepEntity, testCaseStepResult, parentStatus);
    }

    ResultConstant status = (testCaseStepResult.getResult() != ResultConstant.SUCCESS) ? testCaseStepResult.getResult() : currentStatus;

    if ((status == ResultConstant.SUCCESS)) {
      testCaseStepResult.setMessage(AutomatorMessages.MSG_STEP_GROUP_SUCCESS);
    } else if (StringUtils.isBlank(testCaseStepResult.getMessage())) {
      testCaseStepResult.setMessage(AutomatorMessages.MSG_STEP_GROUP_FAILURE);
    }
    return status;
  }

  protected ResultConstant executeForLoop(TestCaseStepEntity testCaseStepEntity, TestCaseStepResult forLoopResultObj,
                                          Map<Long, TestCaseStepResult> testCaseStepResultMap, TestCaseResult tresult,
                                          HashMap<Long, TestCaseStepResult> parentStatus, boolean failedToProcess,
                                          ScreenCaptureUtil screenCaptureUtil, ResultConstant currentStatus) throws Exception {

    log.debug("Step type is FOR_LOOP. Executing it:" + testCaseStepEntity);
    //In for loop , if there is a continue step encountered in prev iteration, we should not skip execution or loop.
    TestCaseStepResult previousLoopResultIfAny = testCaseStepResultMap.get(testCaseStepEntity.getId());
    if (previousLoopResultIfAny != null && previousLoopResultIfAny.getIsContinueLoop()) {
      forLoopResultObj.setSkipExe(false);
    } else if (previousLoopResultIfAny != null && previousLoopResultIfAny.getIsBreakLoop()) {
      forLoopResultObj.setSkipExe(true);
    }
    executeGroup(testCaseStepEntity, forLoopResultObj, testCaseStepResultMap, tresult, parentStatus, failedToProcess, screenCaptureUtil);

    //previous loop encountered a break ,We need to disable skipExe, else further testcases(After loop) will not be executed.
    if (previousLoopResultIfAny != null && previousLoopResultIfAny.getIsBreakLoop()) {
      forLoopResultObj.setIsBreakLoop(true);
      forLoopResultObj.setSkipExe(false);//If skip is true, execution will not be continued after the loop.
      forLoopResultObj.setResult(ResultConstant.SUCCESS);
    }
    forLoopResultObj.setIndex(testCaseStepEntity.getIndex());
    forLoopResultObj.setIteration(testCaseStepEntity.getIteration());
    forLoopResultObj.setTestDataProfileName(testCaseStepEntity.getTestDataProfileName());
    ResultConstant status = (currentStatus.getId() < forLoopResultObj.getResult().getId()) ? forLoopResultObj.getResult() : currentStatus;

    if ((forLoopResultObj.getResult() == ResultConstant.SUCCESS)) {
      forLoopResultObj.setMessage(AutomatorMessages.getMessage(AutomatorMessages.MSG_ITERATION_SUCCESS, testCaseStepEntity.getIndex()));
    } else if (StringUtils.isBlank(forLoopResultObj.getMessage())) {
      forLoopResultObj.setMessage(AutomatorMessages.getMessage(AutomatorMessages.MSG_ITERATION_FAILURE, testCaseStepEntity.getIndex()));
    }
    return status;
  }

  protected ResultConstant executeWhileLoop(TestCaseStepEntity testcaseStep, TestCaseStepResult whileLoopResultObj, Map<Long, TestCaseStepResult> mapStepResult,
                                            TestCaseResult tresult, HashMap<Long, TestCaseStepResult> parentStatus,
                                            boolean failedToProcess, ScreenCaptureUtil screenCaptureUtil, ResultConstant currentStatus) throws Exception {
    Long envRunId = EnvironmentRunner.getRunnerEnvironmentRunResult().getId();
    log.debug("Executing while loop:" + testcaseStep);
    TestCaseStepEntity whileConditionStep = testcaseStep.getTestCaseSteps().get(0);
    List<TestCaseStepResult> whileLoopIterationResults = new ArrayList<>();
    Map<Long,Map<String, String>> testDataValueMap = getRunTimeTestDataValueMap(whileConditionStep);
    boolean breakLoop = false;
    ResultConstant whileLoopResult = ResultConstant.SUCCESS;
    boolean conditionFailed = false;
    int noOfIterationsCompleted = 0;
    int maxIterations = (int) (1+ Optional.ofNullable(whileConditionStep.getMaxIterations()).orElse((long) NaturalTextActionConstants.WHILE_LOOP_MAX_LIMIT));
    //Iterations loop, we are limiting max loop count to ActionConstants.WHILE_LOOP_MAX_LIMIT
    for (int i = 1; i <= maxIterations; i++) {
      if (breakLoop) {
        break;
      }
      resetRunTimeTestDataValues(whileConditionStep,testDataValueMap);
      TestCaseStepResult whileConditionStepResult = new TestCaseStepResult();
      whileConditionStepResult.setEnvRunId(envRunId);
      whileConditionStepResult.setTestCaseResultId(tresult.getId());
      whileConditionStepResult.setTestCaseStepId(whileConditionStep.getId());
      whileConditionStepResult.setSkipExe(whileLoopResultObj.getSkipExe());
      whileConditionStepResult.setSkipMessage(whileLoopResultObj.getSkipMessage());
      whileConditionStepResult.setParentResultId(whileLoopResultObj.getId());
      whileConditionStepResult.setTestCaseStepType(whileConditionStep.getType());
      whileConditionStepResult.setStepDetails(whileConditionStep.getStepDetails());
      whileConditionStepResult.setIndex(i);
      setScreenshotPathsForIteration(whileConditionStep, i);

      TestStepType type = whileConditionStep.getType();
      TestcaseStepRunner testcaseStepRunner = new TestcaseStepRunnerFactory().getRunner(workspaceType, os, type);

      testcaseStepRunner.run(whileConditionStep, whileConditionStepResult, mapStepResult, tresult, parentStatus, failedToProcess, false, screenCaptureUtil);
      if (whileConditionStepResult.getResult() != ResultConstant.SUCCESS) {
        whileConditionStepResult.setSkipExe(true);
        if (whileConditionStepResult.getResult() == ResultConstant.FAILURE) {
          whileConditionStepResult.setSkipMessage(String.format("%s<br>%s", AutomatorMessages.MSG_WHILE_CONDITION_FAILED, whileConditionStepResult.getMessage()));
          conditionFailed = true;
        }
      }
      mapStepResult.put(whileConditionStep.getId(), whileConditionStepResult);

      log.debug("While condition result :::: " + objectMapperService.convertToJson(whileConditionStepResult));
      if(noOfIterationsCompleted+1<maxIterations){
        executeGroup(whileConditionStep, whileConditionStepResult, mapStepResult, tresult, parentStatus, failedToProcess, screenCaptureUtil);
      }

      //Update Iteration result to SUCCESS if break or continue is executed
      if (whileConditionStepResult.getIsBreakLoop() || whileConditionStepResult.getIsContinueLoop()) {
        whileConditionStepResult.setResult(ResultConstant.SUCCESS);
      }
      //If condition is passed, and while executing the group of steps inside condition, if there is a failure in group, parent status will be skipped
      if (whileConditionStepResult.getResult() == ResultConstant.SUCCESS && whileConditionStepResult.getSkipExe()) {
        whileLoopResult = ResultConstant.FAILURE;
        whileLoopResultObj.setSkipExe(true);
      }
      //While loop break condition.
      if (whileConditionStepResult.getIsBreakLoop() || whileConditionStepResult.getSkipExe()) {
        breakLoop = true;
      }
      whileLoopIterationResults.add(whileConditionStepResult);
      whileLoopResult = (whileLoopResult.getId() < whileConditionStepResult.getResult().getId()) ? whileConditionStepResult.getResult() : whileLoopResult;
      noOfIterationsCompleted = i;
      //If condition step is failed, we should not fail while loop status
      if (conditionFailed) {
        whileLoopResult = ResultConstant.FAILURE;
      }
      if(noOfIterationsCompleted == maxIterations){
        whileConditionStepResult.setResult(ResultConstant.FAILURE);
        whileConditionStepResult.setMessage(String.format(AutomatorMessages.MSG_WHILE_LOOP_ITERATIONS_EXHAUSTED,maxIterations-1));
      }
    }
    //Add all iteration results to parent LOOP step
    whileLoopResultObj.setStepResults(whileLoopIterationResults);
    if (whileLoopResultObj.getResult() == null) {
      whileLoopResultObj.setResult(whileLoopResult);
    }
    ResultConstant status = (currentStatus.getId() < whileLoopResultObj.getResult().getId()) ? whileLoopResultObj.getResult() : currentStatus;
    if ((whileLoopResultObj.getResult() == ResultConstant.SUCCESS)) {
      if (noOfIterationsCompleted == maxIterations) {
        status = ResultConstant.SUCCESS;
        whileLoopResultObj.setResult(ResultConstant.SUCCESS);
        whileLoopResultObj.setMessage(AutomatorMessages.MSG_WHILE_LOOP_ITERATIONS_EXHAUSTED);
      } else {
        whileLoopResultObj.setMessage(AutomatorMessages.MSG_WHILE_LOOP_SUCCESS);
      }
    } else if (whileLoopResultObj.getResult() == ResultConstant.FAILURE) {
      whileLoopResultObj.setMessage(AutomatorMessages.MSG_WHILE_LOOP_FAILURE);
    }
    return status;
  }

  protected void setScreenshotPathsForIteration(TestCaseStepEntity testStepEntity, int iterationNumber) {
    String key = String.format("%s_%s", iterationNumber, testStepEntity.getIndex());
    Map<String, String> conditionStepScreenshots = testStepEntity.getAdditionalScreenshotPaths();
    testStepEntity.setScreenshotPath(conditionStepScreenshots.get(key));
    if (testStepEntity.getTestCaseSteps() != null && testStepEntity.getTestCaseSteps().size() > 0) {
      for (TestCaseStepEntity childStep : testStepEntity.getTestCaseSteps()) {
        setScreenshotPathsForIteration(childStep, iterationNumber);
      }
    }
  }

  private void executeGroup(TestCaseStepEntity testcaseStep, TestCaseStepResult result, Map<Long, TestCaseStepResult> mapStepResult,
                            TestCaseResult tresult, HashMap<Long, TestCaseStepResult> parentStatus,
                            boolean failedToProcess, ScreenCaptureUtil ScreenCaptureUtil) throws Exception {
    ExecutionLabType exeType = EnvironmentRunner.getRunnerEnvironmentEntity().getExecutionLabType();
    TestPlanRunSettingEntity settings = EnvironmentRunner.getRunnerEnvironmentEntity().getTestPlanSettings();
    Long envRunId = EnvironmentRunner.getRunnerEnvironmentRunResult().getId();
    List<TestCaseStepResult> stepResults = new ArrayList<>();
    result.setStepResults(stepResults);
    ResultConstant status = ResultConstant.SUCCESS;
    Boolean skipExe = result.getSkipExe();
    String message = result.getSkipMessage();
    boolean isMajorStepGroupFailure =
      (testcaseStep.getPriority().equals(TestStepPriority.MAJOR) &&
        settings.getRecoveryAction() == RecoverAction.Run_Next_Testcase);
    boolean isStepGroup = (testcaseStep.getStepGroupId() != null && testcaseStep.getStepGroupId() > 0);
    boolean breakLoopStepExecuted = false;
    boolean continueLoopStepExecuted = false;
    for (TestCaseStepEntity childStep : testcaseStep.getTestCaseSteps()) {
      populateThreadContextData(childStep);
      log.debug("Executing group step:" + childStep.getId());
      if (breakLoopStepExecuted || continueLoopStepExecuted) {
        log.debug(String.format("Skip execution due to break/continue loop:stepGroupId=%s", childStep.getId()));
        skipExe = true;
      }
      TestCaseStepResult childStepResult = new TestCaseStepResult();
      childStepResult.setEnvRunId(envRunId);
      childStepResult.setTestCaseResultId(tresult.getId());
      childStepResult.setTestCaseStepId(childStep.getId());
      childStepResult.setSkipExe(skipExe);
      childStepResult.setSkipMessage(message);
      TestCaseStepResult parentResult = parentStatus.get(childStep.getParentId());
      childStepResult.setParentResultId(parentResult != null ? parentResult.getId() : null);
      childStepResult.setTestCaseStepType(childStep.getType());
      childStepResult.setVisualEnabled(childStep.getVisualEnabled());

      childStepResult.setStepDetails(childStep.getStepDetails());
      RunnerUtil util = new RunnerUtil();
      TestStepType type = childStep.getType();
      TestcaseStepRunner testcaseStepRunner = new TestcaseStepRunnerFactory().getRunner(workspaceType, os, type);
      boolean isFailure =

        (
          //this check is used to skip loop child steps check.
          //because loop step result is set only after loop is finished
          !util.isLoopSteps(isStepGroup, testcaseStep, childStep)

            && (util.canSkipNormalStep(parentResult, childStep, childStepResult)
            || util.canSkipIfElse(parentResult, childStep, childStepResult)
            || util.nestedConditionalStep(parentResult, childStep, childStepResult)
            || util.canSkipIfElseIf(parentResult, childStep, childStepResult)
            || util.canSkipElseIfElseIf(parentResult, childStep, childStepResult)
            || util.canSkipElseIfElse(parentResult, childStep, childStepResult)
            || util.canSkipIfCondition(parentResult, childStep, childStepResult)
            || util.canSkipForLoop(parentResult, childStep, childStepResult)))

          //This check for updating loop steps if loop parent is failed
          || util.canSkipForLoopTopSteps(isStepGroup, parentResult, testcaseStep, childStep, childStepResult);

      if (!skipExe && !failedToProcess && isFailure) {
        testcaseStepRunner.run(childStep, childStepResult, mapStepResult, tresult, parentStatus, false, isStepGroup, ScreenCaptureUtil);
        mapStepResult.put(childStep.getId(), childStepResult);
        stepResults.add(childStepResult);
        parentStatus.put(childStep.getId(), childStepResult);
        continue;
      }

      testcaseStepRunner.run(childStep, childStepResult, mapStepResult, tresult, parentStatus, failedToProcess, isStepGroup, ScreenCaptureUtil);

      mapStepResult.put(childStep.getId(), childStepResult);
      stepResults.add(childStepResult);

      log.debug("Result in Step Group :::: " + objectMapperService.convertToJson(childStepResult));
      if ((childStep.getConditionType() == null || childStep.getConditionType() == ConditionType.NOT_USED ||
              ConditionType.LOOP_FOR == (childStep.getConditionType()))&& (!childStep.getStepDetails().getIgnoreStepResult())) {
        status = (status.getId() < childStepResult.getResult().getId()) ? childStepResult.getResult() : status;
      }

      boolean isMajorStepFailure = isStepGroup && isStepGroupFailure(testcaseStep, childStep, childStepResult);

      if (!skipExe && isMajorStepFailure && isMajorStepGroupFailure) {
        result.setResult(ResultConstant.FAILURE);
        String majorMessage = new StringBuffer(AutomatorMessages.MSG_STEP_MAJOR_STEP_FAILURE)
          .append((childStepResult.getMessage() != null) ? childStepResult.getMessage() + "." : "")
          .append(AutomatorMessages.MSG_CHECK_FOR_MORE_DETAILS).toString();
        result.setMessage(majorMessage);
        setFailedMessage(childStepResult, tresult, testcaseStep.getStepDetails().getIgnoreStepResult());
      }
      skipExe = childStepResult.getSkipExe();
      message = childStepResult.getSkipMessage();
      //Set break loop and continue loop to group results
      if (childStepResult.getResult() == ResultConstant.SUCCESS
        && (childStepResult.getIsBreakLoop() || childStepResult.getIsContinueLoop())) {
        continueLoopStepExecuted = childStepResult.getIsContinueLoop();
        breakLoopStepExecuted = childStepResult.getIsBreakLoop();
        populateLoopConditionResult(result, childStepResult, testcaseStep);
        log.debug("Is Break loop step executed successfully:" + breakLoopStepExecuted);
        log.debug("Is Continue loop step executed successfully:" + continueLoopStepExecuted);
      }

      result.setSkipExe(skipExe);
      result.setSkipMessage(message);

    }
    if (result.getResult() == null) {
      result.setResult(status);
    }
    //If current loop encounters a continue OR current loop encountered a break.
    // We need to disable skipExe, else further testcases(After loop) will not be executed.
    if (result.getIsContinueLoop() || result.getIsBreakLoop()) {
      result.setSkipExe(false);//If skip is true, execution will not be continued after the loop.
      result.setResult(ResultConstant.SUCCESS);
    }
    resetThreadContextData();
  }

  protected void populateLoopConditionResult(TestCaseStepResult parentLoopResult, TestCaseStepResult childStepResult, TestCaseStepEntity parentLoopStep) {
    if (parentLoopStep.getConditionType() == ConditionType.LOOP_WHILE
      || parentLoopStep.getConditionType() == ConditionType.LOOP_FOR
      || parentLoopStep.getStepGroupId() != null) {
      if (childStepResult.getIsBreakLoop()) {
        parentLoopResult.setIsBreakLoop(childStepResult.getIsBreakLoop());
      } else if (childStepResult.getIsContinueLoop()) {
        parentLoopResult.setIsContinueLoop(childStepResult.getIsContinueLoop());
      }
    }
  }

  private void setFailedMessage(TestCaseStepResult result, TestCaseResult testCaseResult, Boolean ignoreStepResult) throws AutomatorException {

    result.setSkipExe(true);
    result.setSkipMessage(AutomatorMessages.MSG_STEP_MAJOR_STEP_FAILURE);
    if(!ignoreStepResult) {
      String majorMessage = AutomatorMessages.MSG_STEP_MAJOR_STEP_FAILURE +
              ((result.getMessage() != null) ? result.getMessage() : "") + " . " + AutomatorMessages.MSG_CHECK_FOR_MORE_DETAILS;
      testCaseResult.setMessage(majorMessage);
      testCaseResult.setResult(ResultConstant.FAILURE);
    }
  }

  private boolean isStepGroupFailure(TestCaseStepEntity testcaseStep, TestCaseStepEntity childStep,
                                         TestCaseStepResult childStepResult) {
    return childStep.getPriority() == TestStepPriority.MAJOR &&
      childStepResult.getResult() != null && !childStepResult.getResult().equals(ResultConstant.SUCCESS)
      && (testcaseStep.getConditionType() == null || !testcaseStep.getConditionType().equals(ConditionType.LOOP_FOR))
            && !childStep.getStepDetails().getIgnoreStepResult();
  }


  protected void setTestDataValue(TestCaseStepEntity step, Map<String, String> envDetails, TestCaseResult testCaseResult, TestCaseStepResult testCaseStepResult)
    throws AutomatorException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IOException, InvocationTargetException {
    if (step.getTestDataMap() == null)
      return;
    int index =0;
    Map<String, TestDataPropertiesEntity> testDataPropertiesEntityMap = step.getTestDataMap();
    for (Map.Entry<String, TestDataPropertiesEntity> entry : testDataPropertiesEntityMap.entrySet()) {
      TestDataPropertiesEntity testDataPropertiesEntity = entry.getValue();
      TestDataType testDataType = TestDataType.getTestDataType(ObjectUtils.defaultIfNull(testDataPropertiesEntity.getTestDataType(), "raw"));
      switch (testDataType) {
        case runtime:
          if (!step.getAction().startsWith("Store ")) {
            RuntimeDataProvider runtimeDataProvider = new RuntimeDataProvider();

            testDataPropertiesEntity.setTestDataValue(
              runtimeDataProvider.getRuntimeData(testDataPropertiesEntity.getTestDataValue()));
          }
          break;
        case random:
          testDataPropertiesEntity.setTestDataValue(RandomStringUtils.randomAlphanumeric(
            Integer.parseInt(testDataPropertiesEntity.getTestDataValue())));
          break;
        case function:
          if (!testDataPropertiesEntity.getDefaultDataGeneratorsEntity().getIsAddonFn()) {
            DefaultDataGeneratorsEntity testDataFunctionEntity = testDataPropertiesEntity.getDefaultDataGeneratorsEntity();
            DefaultDataGeneratorsExecutor testDataFunctionExecutor = new DefaultDataGeneratorsExecutor();
            testDataFunctionExecutor.setTestCaseResult(testCaseResult);
            testDataFunctionExecutor.setSettings(envDetails);
            testDataFunctionExecutor.setDefaultDataGeneratorsEntity(testDataFunctionEntity);
            String testDataValue = testDataFunctionExecutor.generate();
            testDataPropertiesEntity.setTestDataName(testDataPropertiesEntity.getTestDataValue());
            testDataPropertiesEntity.setTestDataValue(testDataValue);
          } else {
            setTestDataValueFromAddonTestDataFunction(step, index, testDataPropertiesEntity, testCaseStepResult);
            index++;
          }
          break;
      }
      step.setTestDataName(testDataPropertiesEntity.getTestDataName());
      step.setTestDataValue(testDataPropertiesEntity.getTestDataValue());
    }
  }


  private void setTestDataValueFromAddonTestDataFunction(TestCaseStepEntity testCaseStepEntity, int index, TestDataPropertiesEntity testDataPropertiesEntity, TestCaseStepResult testCaseStepResult) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException {

    try {
      List<AddonPluginTestDataFunctionEntity> addonPluginTDFEntityList = testCaseStepEntity.getAddonPluginTDFEntityList();
      AddonPluginTestDataFunctionEntity entity = addonPluginTDFEntityList.get(index);
      String jarFilePath = addonService.checkAndDownloadJar(entity.getClassPath(), entity.getModifiedHash());
      Class<?> clazz = addonService.loadJarClass(jarFilePath, entity.getFullyQualifiedName(), false);
      Object instance = clazz.getDeclaredConstructor().newInstance();
      setTestDataParameter(instance, addonService, testDataPropertiesEntity);
      Method executeMethod = clazz.getDeclaredMethod("generate");
      executeMethod.setAccessible(true);
      TestData testData = (TestData) executeMethod.invoke(instance);
      testDataPropertiesEntity.setTestDataName(entity.getDisplayName());
      testDataPropertiesEntity.setTestDataValue(testData.getValue().toString());
    } catch (Exception e) {
      String message = StringUtils.isBlank(e.getMessage()) ? e.getCause().getMessage() : e.getMessage();
      if (message == null) {
        testCaseStepResult.setMessage("Teststep execution failed. No Additional message was available.");
      } else {
        testCaseStepResult.setMessage(message);
      }
      throw e;
    }
  }

  public void setTestDataParameter(Object instance, AddonService addonService, TestDataPropertiesEntity testDataPropertiesEntity) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
    Map<String, String> arguments = testDataPropertiesEntity.getDefaultDataGeneratorsEntity().getArguments();
    for (Map.Entry<String, String> entry : arguments.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();
      Object testDataParameterInstance = addonService.getTestDataParameterInstance(value);
      FieldUtils.writeField(instance, key, testDataParameterInstance, true);
      log.info("Setting test data instance - " + testDataParameterInstance);
    }
  }

  private void takeScreenshot(WorkspaceType workspaceType, TestCaseStepEntity testcaseStep,
                              TestCaseStepResult result, Screenshot option, ScreenCaptureUtil screenCaptureUtil) {
    TestDeviceSettings envSettings = EnvironmentRunner.getRunnerEnvironmentEntity().getEnvSettings();
    try {
      boolean takeScreenshot = false;
      String screenshotS3URL = testcaseStep.getScreenshotPath();

      if (option == Screenshot.NONE) {
        takeScreenshot = false;
      } else if (option == Screenshot.ALL_TYPES) {
        takeScreenshot = true;
      } else if ((option == Screenshot.FAILED_STEPS) && (result.getResult() != ResultConstant.SUCCESS)) {
        takeScreenshot = true;
      }

      int screenshotType = 0;
      if (testcaseStep.getAction() != null && testcaseStep.getAction().toLowerCase()
        .contains(AutomatorMessages.KEYWORD_SCREENSHOT.toLowerCase())) {
        screenshotType = 1;

      } else if (testcaseStep.getAction() != null && testcaseStep.getAction().toLowerCase()
        .contains(AutomatorMessages.KEYWORD_SCREENSHOT.toLowerCase())) {
        screenshotType = 2;
      } else if (testcaseStep.getAction() != null && testcaseStep.getAction().toLowerCase()
              .contains(AutomatorMessages.KEYWORD_ELEMENT_SCREENSHOT.toLowerCase())) {
        screenshotType = 3;
      }

      if (Arrays.asList(SKIP_SCREENSHOT_KEYWORDS).contains(testcaseStep.getAction())) {
        takeScreenshot = false;
      }

      if (!takeScreenshot && screenshotType == 0) {
        return;
      }

      String screenShotName = FilenameUtils.getName(new java.net.URL(screenshotS3URL).getPath());
      String localFolderPath = envSettings.getScreenshotLocalPath();
      testcaseStep.setScreenshot(option);
      result.setScreenshotName(screenShotName);
      WebDriver driver = DriverManager.getRemoteWebDriver();

      switch (workspaceType) {
        case WebApplication:
          if (screenshotType == 3) {
            screenCaptureUtil.takeElementScreenShot(driver, testcaseStep, localFolderPath, screenshotS3URL);
          }
        case MobileWeb:
          if (screenshotType == 1) {
            screenCaptureUtil.screenShotWithURL(localFolderPath, screenshotS3URL, driver);

          } else if (screenshotType == 2) {
            screenCaptureUtil.fullPageScreenshotWithURL(localFolderPath, screenshotS3URL, driver);
          } else if (screenshotType == 3) {
          screenCaptureUtil.takeElementScreenShot(driver, testcaseStep, localFolderPath, screenshotS3URL);
        } else {
            screenCaptureUtil.takeScreenShot(driver, localFolderPath, screenshotS3URL);
          }
          break;
        case AndroidNative:
        case IOSNative:
          screenCaptureUtil.takeScreenShot(driver, localFolderPath, screenshotS3URL);
          break;
        default:
          break;
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public void setConditionResult(TestCaseStepEntity testcaseStep, TestCaseStepResult result,
                                 HashMap<Long, TestCaseStepResult> parentStatus) {
    ResultConstant[] expectedStatus = testcaseStep.getIfConditionExpectedResults();
    if (testcaseStep.getConditionType() == ConditionType.CONDITION_IF) {
      boolean isSuccess = false;
      if (expectedStatus != null) {
        for (ResultConstant status : expectedStatus) {
          isSuccess = (status == result.getResult());
          if (isSuccess) {
            break;
          }
        }
      }


      result.setIsConditionSuccess(isSuccess);
      parentStatus.put(testcaseStep.getId(), result);


      if (result.getIsConditionSuccess()) {
        result.setResult(ResultConstant.SUCCESS);
        result.setMessage(AutomatorMessages.MSG_CONDITION_IF_SUCCESS);
      } else {
        result.setResult(ResultConstant.FAILURE);
        //result.setMessage(AutomatorMessges.MSG_CONDITION_IF_FAILED);
      }
    } else if (testcaseStep.getConditionType() == ConditionType.CONDITION_ELSE) {
      TestCaseStepResult parentResult = parentStatus.get(testcaseStep.getParentId());
      if (parentResult != null) {
        result.setIsConditionSuccess(!parentResult.getIsConditionSuccess());
        parentStatus.put(testcaseStep.getId(), result);
      }

      if (result.getIsConditionSuccess()) {
        result.setMessage(AutomatorMessages.MSG_CONDITION_ELSE_SUCCESS);
        result.setResult(ResultConstant.SUCCESS);
      } else {
        //result.setMessage(AutomatorMessges.MSG_CONDITION_ELSE_FAILED);
        result.setResult(ResultConstant.FAILURE);
      }
    } else if (testcaseStep.getConditionType() == ConditionType.CONDITION_ELSE_IF) {
      TestCaseStepResult parentResult = parentStatus.get(testcaseStep.getParentId());
      boolean isSuccess = false;
      if (parentResult != null) {
        if (!parentResult.getIsConditionSuccess()) {
          if (expectedStatus != null) {
            for (ResultConstant status : expectedStatus) {
              isSuccess = (status == result.getResult());
              if (isSuccess) {
                break;
              }
            }
          }
        }

        result.setIsConditionSuccess(isSuccess);
        parentStatus.put(testcaseStep.getId(), result);

        if (result.getIsConditionSuccess()) {
          result.setResult(ResultConstant.SUCCESS);
          result.setMessage(AutomatorMessages.MSG_CONDITION_ELSE_IF_SUCCESS);
        } else {
          result.setResult(ResultConstant.FAILURE);
          //result.setMessage(AutomatorMessges.MSG_CONDITION_ELSE_IF_FAILED);
        }

      }
    }

  }

  protected abstract void execute(Map<String, String> envSettings, TestCaseStepResult result,
                                  TestCaseStepEntity testcaseStep, TestCaseResult testCaseResult) throws AutomatorException;

  protected abstract void onStepFailure(ExecutionLabType exeType, WorkspaceType workspaceType, TestPlanRunSettingEntity settings) throws AutomatorException;
  private void resetRunTimeTestDataValues(TestCaseStepEntity testCaseStepEntity, Map<Long, Map<String, String>> testDataValueMap) {
    LinkedHashMap<String,TestDataPropertiesEntity> testDataMap = testCaseStepEntity.getTestDataMap();
    if(testDataMap != null){
      for (Map.Entry<String, TestDataPropertiesEntity> entry : testDataMap.entrySet()) {
        TestDataPropertiesEntity testDataPropertiesEntity = entry.getValue();
        TestDataType testDataType = TestDataType.getTestDataType(ObjectUtils.defaultIfNull(testDataPropertiesEntity.getTestDataType(), "raw"));
        if("runtime".equalsIgnoreCase(testDataType.toString())){
          testDataPropertiesEntity.setTestDataValue(testDataValueMap.get(testCaseStepEntity.getId()).get(entry.getKey()));
        }
      }
    }
    if(testCaseStepEntity.getTestCaseSteps() != null){
      for(TestCaseStepEntity subEntity : testCaseStepEntity.getTestCaseSteps()){
        resetRunTimeTestDataValues(subEntity,testDataValueMap);
      }
    }

  }

  protected Map<Long,Map<String, String>> getRunTimeTestDataValueMap(TestCaseStepEntity testCaseStepEntity){
    Map<Long,Map<String, String>> stepToTestDataValuesMap = new HashMap<>();
    LinkedHashMap<String,TestDataPropertiesEntity> testDataMap = testCaseStepEntity.getTestDataMap();
    Map<String, String> testDataValuesMap = new HashMap<>();
    if(testDataMap != null){
      for (Map.Entry<String, TestDataPropertiesEntity> entry : testDataMap.entrySet()) {
        TestDataPropertiesEntity testDataPropertiesEntity = entry.getValue();
        TestDataType testDataType = TestDataType.getTestDataType(ObjectUtils.defaultIfNull(testDataPropertiesEntity.getTestDataType(), "raw"));
        if("runtime".equalsIgnoreCase(testDataType.toString())){
          testDataValuesMap.put(entry.getKey(),testDataPropertiesEntity.getTestDataValue());
        }
      }
      stepToTestDataValuesMap.put(testCaseStepEntity.getId(),testDataValuesMap);
    }
    if(testCaseStepEntity.getTestCaseSteps() != null){
      for(TestCaseStepEntity subEntity : testCaseStepEntity.getTestCaseSteps()){
        stepToTestDataValuesMap.putAll(getRunTimeTestDataValueMap(subEntity));
      }
    }
    return stepToTestDataValuesMap;
  }
}
