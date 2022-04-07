/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testsigma.automator.actions.constants.ErrorCodes;
import com.testsigma.automator.entity.TestDeviceEntity;
import com.testsigma.config.ApplicationConfig;
import com.testsigma.config.StorageServiceFactory;
import com.testsigma.constants.AutomatorMessages;
import com.testsigma.constants.MessageConstants;
import com.testsigma.constants.NaturalTextActionConstants;
import com.testsigma.dto.*;
import com.testsigma.exception.ExceptionErrorCodes;
import com.testsigma.exception.IntegrationNotFoundException;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.*;
import com.testsigma.model.*;
import com.testsigma.step.processors.ForLoopStepProcessor;
import com.testsigma.step.processors.RestStepProcessor;
import com.testsigma.step.processors.StepProcessor;
import com.testsigma.step.processors.WhileLoopStepProcessor;
import com.testsigma.tasks.TestPlanRunTask;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;


@Data
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Service
public class AgentExecutionService {
  private static final Integer PRE_REQUISITE_DEPTH = 5;

  private final WebApplicationContext webApplicationContext;
  private final StorageServiceFactory storageServiceFactory;
  private final TestPlanResultService testPlanResultService;
  private final TestDeviceService testDeviceService;
  private final TestDeviceResultService testDeviceResultService;
  private final TestSuiteService testSuiteService;
  private final TestSuiteResultService testSuiteResultService;
  private final TestCaseService testCaseService;
  private final TestCaseResultService testCaseResultService;
  private final TestStepService testStepService;
  private final TestStepResultService testStepResultService;
  private final RunTimeDataService runTimeDataService;
  private final AgentService agentService;
  private final TestPlanMapper testPlanMapper;
  private final TestDeviceResultMapper testDeviceResultMapper;
  private final TestSuiteResultMapper testSuiteResultMapper;
  private final TestCaseResultMapper testCaseResultMapper;
  private final EnvironmentService environmentService;
  private final TestDataProfileService testDataProfileService;
  private final ElementService elementService;
  private final TestStepMapper testStepMapper;
  private final TestDeviceSuiteService testDeviceSuiteService;
  private final SuiteTestCaseMappingService suiteTestCaseMappingService;
  private final AgentDeviceService agentDeviceService;
  private final TestPlanService testPlanService;
  private final TestCaseDataDrivenResultService testCaseDataDrivenResultService;
  private final PlatformsService platformsService;
  private final AddonService addonService;
  private final AddonNaturalTextActionService addonNaturalTextActionService;
  private final IntegrationsService integrationsService;
  private final ApplicationConfig applicationConfig;
  private final JWTTokenService jwtTokenService;
  public AbstractTestPlan testPlan;
  private JSONObject runTimeData;
  private TestPlanResult testPlanResult;

  private Boolean isReRun = Boolean.FALSE;

  private ReRunType reRunType = ReRunType.NONE;

  private Long parentTestPlanResultId;

  private List<TestDeviceResult> testDeviceResultsReRunList;

  private List<TestSuiteResult> testSuiteResultsReRunList;

  private List<TestCaseResult> testCaseResultsReRunList;

  private Long scheduleId;

  private ExecutionTriggeredType triggeredType = ExecutionTriggeredType.MANUAL;


  // ################################################  START  ###################################################

  public void start() throws Exception {
    try {
      beforeStart();
      populateResultEntries(true);
      saveRunTimeData();
      processResultEntries();
      afterStart();
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      if (testPlanResult != null) {
        testPlanResult.setIsInProgress(Boolean.FALSE);
        testPlanResultService.update(testPlanResult);
        stopQueuedEnvironments(e.getMessage(), Boolean.FALSE);
      } else {
        if (this.isScheduledExecution() && !getIsReRun()) {
          populateResultEntries(false);
          stopQueuedEnvironments(e.getMessage(), Boolean.FALSE);
        }
      }
      throw e;
    }
  }

  // ################################################ BEFORE START  ###################################################

  private void beforeStart() throws TestsigmaException {
    checkAlreadyRunning();
  }

  private void checkAlreadyRunning() throws TestsigmaException {
    checkIfAlreadyHasAnotherRun();
    checkIfAlreadyHasReRunParentId();
  }

  public void checkIfAlreadyHasReRunParentId() throws TestsigmaException {
    if (this.getParentTestPlanResultId() != null) {
      boolean ReRunParentIdAlreadyExsists = this.testPlanResultService.findByReRunParentId(this.getParentTestPlanResultId());
      if (ReRunParentIdAlreadyExsists) {
        log.info(String.format("Execution [%s] cannot be executed as its rerun parent id [%d] already exists", this.testPlan.getId(),
          testPlanResult.getReRunParentId()));
        throw new TestsigmaException(AutomatorMessages.RE_RUN_PARENT_ID_ALREADY_EXSISTS);
      }
    }

  }

  private void checkIfAlreadyHasAnotherRun() throws TestsigmaException {
    TestPlanResult testPlanResult = this.testPlanResultService.findByTestPlanIdAndStatusIsNot(this.testPlan.getId(),
      StatusConstant.STATUS_COMPLETED);
    if (testPlanResult != null) {
      log.info(String.format("Execution [%s] is already running and is in status %s ", this.testPlan.getId(),
        testPlanResult.getStatus()));
      throw new TestsigmaException(AutomatorMessages.EXECUTION_ALREADY_RUNNING);
    }
  }

  // ############################################ RESULT ENTRIES CREATION #############################################

  private void populateResultEntries(boolean setLastRunId) throws TestsigmaException {
    TestPlanResult testPlanResult = createTestPlanResult();
    populateLastRunId(testPlanResult, setLastRunId);
    this.setTestPlanResult(testPlanResult);
    populateEnvironmentResults(testPlanResult);
  }

  private void populateLastRunId(TestPlanResult testPlanResult, boolean setLastRunId) {
    AbstractTestPlan testPlan = this.getTestPlan();
    if (setLastRunId) {
      testPlan.setLastRunId(testPlanResult.getId());
    }
    if (testPlan instanceof TestPlan)
      this.setTestPlan(testPlanService.update((TestPlan) testPlan));
  }

  private void populateEnvironmentResults(TestPlanResult testPlanResult) throws TestsigmaException {
    List<TestDevice> testDevices =
      testDeviceService.findByTestPlanIdAndDisable(this.getTestPlan().getId(), Boolean.FALSE);
    for (TestDevice testDevice : testDevices) {
      log.info("Populating Environment result for environment:" + testDevice);
      TestDeviceResult testDeviceResult = createEnvironmentResult(testPlanResult, testDevice);
      if (testDeviceResult != null) {
        populateTestSuiteResults(testDeviceResult, testDevice);
      }
    }
  }

  private void populateTestSuiteResults(TestDeviceResult testDeviceResult, TestDevice testDevice)
    throws TestsigmaException {
    List<AbstractTestSuite> testSuites = this.testSuiteService.findAllByTestDeviceId(testDeviceResult.getTestDeviceId());
    for (AbstractTestSuite testSuite : testSuites) {
      log.info("Populate TestSuite result for suite:" + testSuite.getName());
      TestSuiteResult testSuiteResult = createTestSuiteResult(testSuite, testDeviceResult, testDevice);
      if (testSuiteResult != null) {
        testSuite.setLastRunId(testSuiteResult.getId());
        if (testPlan instanceof TestPlan)
          this.testSuiteService.updateSuite((TestSuite) testSuite);
        populateTestCaseResults(testSuite, testSuiteResult, testDeviceResult);
      }
    }
  }

  private void populateTestCaseResults(AbstractTestSuite testSuite, TestSuiteResult testSuiteResult,
                                       TestDeviceResult testDeviceResult) throws TestsigmaException {
    List<TestCase> testCases = this.testCaseService.findAllBySuiteId(testSuiteResult.getSuiteId());
    for (TestCase testCase : testCases) {
      TestCaseResult testCaseResult = createTestCaseResult(testSuite, testCase, testDeviceResult, testSuiteResult,
        null);
      if (testCaseResult != null && testPlan instanceof TestPlan) {
        testCase.setLastRunId(testCaseResult.getId());
        testCaseService.update(testCase);
      }
    }
  }

  protected void populateStepGroupTestStepResults(TestStep testStep, TestCaseResult testCaseResult,
                                                  TestDeviceResult testDeviceResult,
                                                  TestStepResult parentTestStepResult) {

    List<TestStep> testSteps = this.testStepService.findAllByTestCaseId(testStep.getStepGroupId());

    for (TestStep step : testSteps) {
      createTestStepResult(step, testDeviceResult, testCaseResult, parentTestStepResult);
    }
  }

  protected void createTestStepResult(TestStep testStep,
                                      TestDeviceResult testDeviceResult,
                                      TestCaseResult testCaseResult,
                                      TestStepResult parentTestStepResult) {
    log.info("Creating TestStepResult for:" + testStep);
    TestStepResult testStepResult = new TestStepResult();
    testStepResult.setEnvRunId(testDeviceResult.getId());
    testStepResult.setResult(ResultConstant.QUEUED);
    testStepResult.setMessage(AutomatorMessages.MSG_EXECUTION_CREATED);
    testStepResult.setStepId(testStep.getId());
    testStepResult.setTestCaseId(testCaseResult.getTestCaseId());
    testStepResult.setStepGroupId(testStep.getStepGroupId());
    testStepResult.setGroupResultId((parentTestStepResult != null) ? parentTestStepResult.getId() : null);
    testStepResult.setTestCaseResultId(testCaseResult.getId());
    testStepResult.setPriority(testStep.getPriority());
    StepDetails stepDetails = new StepDetails();
    stepDetails.setNaturalTextActionId(testStep.getNaturalTextActionId());
    stepDetails.setAction(testStep.getAction());
    stepDetails.setPriority(testStep.getPriority());
    stepDetails.setPreRequisiteStepId(testStep.getPreRequisiteStepId());
    stepDetails.setConditionType(testStep.getConditionType());
    stepDetails.setParentId(testStep.getParentId());
    stepDetails.setType(testStep.getType());
    stepDetails.setStepGroupId(testStep.getStepGroupId());
    stepDetails.setAction(testStep.getAction());
    stepDetails.setPosition(testStep.getPosition());
    stepDetails.setTestDataName(testCaseResult.getTestDataSetName());
    stepDetails.setDataMap(testStep.getDataMapBean());
    testStepResult.setStepDetails(stepDetails);
    if (parentTestStepResult != null) {
      testStepResult.setParentResultId(parentTestStepResult.getId());
    }
    testStepResult = this.testStepResultService.create(testStepResult);
    if (TestStepType.STEP_GROUP.equals(testStep.getType())) {
      populateStepGroupTestStepResults(testStep, testCaseResult, testDeviceResult, testStepResult);
    }
  }

  private void populateDataDrivenTestCaseResults(AbstractTestSuite testSuite,
                                                 TestCase testCase,
                                                 TestDeviceResult testDeviceResult,
                                                 TestSuiteResult testSuiteResult,
                                                 TestCaseResult parentTestCaseResult) throws TestsigmaException {
    log.info("Creating DatadrivenTestcaseResult for testcase:" + testCase.getName());
    TestData testData = testCase.getTestData();
    List<TestDataSet> testDataSets = testData.getData();
    int start = testCase.getTestDataStartIndex() != null ? testCase.getTestDataStartIndex() : 0;
    int end = testCase.getTestDataEndIndex() != null ? testCase.getTestDataEndIndex() : testDataSets.size() - 1;
    for (int i = start; i <= end && i < testDataSets.size(); i++) {
      testCase.setIsDataDriven(false);
      TestDataSet testDataSet = testDataSets.get(i);
      testCase.setIsDataDriven(false);
      testCase.setTestDataStartIndex(testDataSets.indexOf(testDataSet));
      TestCaseResult testCaseResult = createTestCaseResult(testSuite, testCase, testDeviceResult, testSuiteResult,
        parentTestCaseResult);
      if (testCaseResult != null) {
        createTestCaseDataDrivenResult(testDataSet, testCaseResult);
      }
    }
    testCase.setIsDataDriven(true);
    testCase.setTestDataStartIndex(start);
  }

  private TestCaseDataDrivenResult createTestCaseDataDrivenResult(TestDataSet testDataSet, TestCaseResult testCaseResult) {
    TestCaseDataDrivenResult testCaseDataDrivenResult = new TestCaseDataDrivenResult();
    testCaseDataDrivenResult.setEnvRunId(testCaseResult.getEnvironmentResultId());
    testCaseDataDrivenResult.setTestData(new ObjectMapperService().convertToJson(testDataSet));
    testCaseDataDrivenResult.setTestDataName(testDataSet.getName());
    testCaseDataDrivenResult.setTestCaseId(testCaseResult.getTestCaseId());
    testCaseDataDrivenResult.setTestCaseResultId(testCaseResult.getParentId());
    testCaseDataDrivenResult.setIterationResultId(testCaseResult.getId());
    return testCaseDataDrivenResultService.create(testCaseDataDrivenResult);
  }

  private TestCaseResult createTestCaseResult(AbstractTestSuite testSuite,
                                              TestCase testCase,
                                              TestDeviceResult testDeviceResult,
                                              TestSuiteResult testSuiteResult,
                                              TestCaseResult parentTestCaseResult) throws TestsigmaException {
    log.info("Creating TestcaseResult for:" + testCase);
    checkForDataDrivenIntegrity(testCase);
    TestCaseResult testCaseResult = new TestCaseResult();
    testCaseResult = setReRunParentId(testSuiteResult, testCase, testCaseResult, parentTestCaseResult);
    if (testCaseResult == null)
      return null;

    testCaseResult.setEnvironmentResultId(testDeviceResult.getId());
    testCaseResult.setTestPlanResultId(testDeviceResult.getTestPlanResultId());
    testCaseResult.setTestCaseId(testCase.getId());
    testCaseResult.setSuiteId(testSuiteResult.getSuiteId());
    testCaseResult.setSuiteResultId(testSuiteResult.getId());
    testCaseResult.setResult(ResultConstant.QUEUED);
    testCaseResult.setStatus(StatusConstant.STATUS_CREATED);
    testCaseResult.setMessage(AutomatorMessages.MSG_EXECUTION_CREATED);
    testCaseResult.setIsStepGroup(testCase.getIsStepGroup());
    if (parentTestCaseResult != null) {
      testCaseResult.setParentId(parentTestCaseResult.getId());
    }
    if (!testCase.getIsDataDriven()) {
      TestData testData = testCase.getTestData();
      if (testData != null) {
        TestDataSet testDataSet = testData.getData().get(testCase.getTestDataStartIndex());
        testCaseResult.setTestDataSetName(testDataSet.getName());
        if (parentTestCaseResult != null) {
          testCaseResult.setIteration(testDataSet.getName());
        }
      }
    }
    Optional<SuiteTestCaseMapping> suiteTestCaseMapping =
      suiteTestCaseMappingService.findFirstByTestSuiteAndTestCase(testSuite, testCase);
    TestCaseResult finalTestCaseResult = testCaseResult;
    suiteTestCaseMapping
      .ifPresent(suiteMapping -> finalTestCaseResult.setPosition(suiteMapping.getPosition().longValue()));

    if (suiteTestCaseMapping.isPresent()) {
      testCaseResult.setPosition(suiteTestCaseMapping.get().getPosition().longValue());
    }
    testCaseResult.setTestCaseTypeId(testCase.getType());
    testCaseResult.setTestCaseStatus(testCase.getStatus());
    testCaseResult.setPriorityId(testCase.getPriority());
    testCaseResult.setIsDataDriven(testCase.getIsDataDriven());
    testCaseResult.setTestDataId(testCase.getTestDataId());
    testCaseResult.setTestCaseDetails(testCaseDetails(testCaseResult, testCase));
    testCaseResult = this.testCaseResultService.create(testCaseResult);
    if (testCase.getIsDataDriven()) {
      populateDataDrivenTestCaseResults(testSuite, testCase, testDeviceResult, testSuiteResult, testCaseResult);
    }
    return testCaseResult;
  }

  private TestCaseResult setReRunParentId(TestSuiteResult testSuiteResult, TestCase testCase, TestCaseResult testCaseResult, TestCaseResult parentTestCaseResult) {
    if (getIsReRun() && (testSuiteResult.getReRunParentId() != null)) {
      TestCaseResult reRunParentTestCaseResult = testCaseResultsReRunList.stream().filter(
        er -> er.getTestCaseId().equals(testCase.getId()) && er.getIteration() == null).findAny().orElse(null);
      if (reRunParentTestCaseResult != null) {
        testCaseResult.setReRunParentId(reRunParentTestCaseResult.getId());
      } else {
        log.info("Test Case (" + testCase.getId() + ") is not eligible for Re-run. Skipping...");
        return null;
      }
    }
    if (!testCase.getIsDataDriven() && testCase.getTestData() != null && parentTestCaseResult != null) {
      TestData testData = testCase.getTestData();
      TestDataSet testDataSet = testData.getData().get(testCase.getTestDataStartIndex());
      if (getIsReRun() && (testSuiteResult.getReRunParentId() != null)) {
        TestCaseResult reRunParentTestCaseResult = testCaseResultsReRunList.stream().filter(
          er -> er.getTestCaseId().equals(testCase.getId()) && er.getIteration() != null && er.getIteration().equals(testDataSet.getName())).findAny().orElse(null);
        if (reRunParentTestCaseResult != null) {
          testCaseResult.setReRunParentId(reRunParentTestCaseResult.getId());
        } else {
          log.info("Test Case (" + testCase.getId() + ") is not eligible for Re-run. Skipping...");
          return null;
        }
      }
    }
    return testCaseResult;
  }

  private TestCaseDetails testCaseDetails(TestCaseResult testCaseResult, TestCase testCase) {
    TestCaseDetails testCaseDetails = new TestCaseDetails();
    testCaseDetails.setName(testCase.getName());
    testCaseDetails.setTestData(testCaseResult.getIteration());
    testCaseDetails.setTestDataSetName(testCaseResult.getTestDataSetName());
    testCaseDetails.setPrerequisite(testCase.getPreRequisite());
    return testCaseDetails;
  }

  private TestSuiteResult createTestSuiteResult(AbstractTestSuite testSuite, TestDeviceResult testDeviceResult,
                                                TestDevice testDevice) {
    TestSuiteResult testSuiteResult = new TestSuiteResult();
    if (getIsReRun() && (testDeviceResult.getReRunParentId() != null)) {
      TestSuiteResult parentTestSuiteResult = testSuiteResultsReRunList.stream().filter(
        er -> er.getSuiteId().equals(testSuite.getId())).findAny().orElse(null);
      if (parentTestSuiteResult != null) {
        testSuiteResult.setReRunParentId(parentTestSuiteResult.getId());
        fetchTestCaseResultsReRunList(parentTestSuiteResult.getId());
      } else {
        log.info("Test Suite (" + testSuite.getId() + ") is not eligible for Re-run. Skipping...");
        return null;
      }
    }
    testSuiteResult.setEnvironmentResultId(testDeviceResult.getId());
    testSuiteResult.setResult(ResultConstant.QUEUED);
    testSuiteResult.setStatus(StatusConstant.STATUS_CREATED);
    testSuiteResult.setMessage(AutomatorMessages.MSG_EXECUTION_CREATED);
    testSuiteResult.setSuiteId(testSuite.getId());
    testSuiteResult.setStartTime(new Timestamp(System.currentTimeMillis()));
    testSuiteResult.setTestPlanResultId(testDeviceResult.getTestPlanResultId());
    Optional<TestDeviceSuite> environmentSuiteMapping =
      testDeviceSuiteService.findFirstByTestDeviceAndTestSuite(testDevice, testSuite);
    environmentSuiteMapping
      .ifPresent(suiteMapping -> testSuiteResult.setPosition(suiteMapping.getPosition().longValue()));
    TestSuiteResultSuiteDetails suiteDetails = new TestSuiteResultSuiteDetails();
    suiteDetails.setName(testSuite.getName());
    suiteDetails.setPreRequisite(testSuite.getPreRequisite());
    testSuiteResult.setSuiteDetails(suiteDetails);
    return this.testSuiteResultService.create(testSuiteResult);
  }

  private TestDeviceResult createEnvironmentResult(TestPlanResult testPlanResult,
                                                   TestDevice testDevice) throws TestsigmaException {
    TestDeviceResult testDeviceResult = new TestDeviceResult();

    if (getIsReRun() && (testPlanResult.getReRunParentId() != null)) {
      TestDeviceResult parentTestDeviceResult = testDeviceResultsReRunList.stream().filter(
        er -> er.getTestDeviceId().equals(testDevice.getId())).findAny().orElse(null);
      if (parentTestDeviceResult != null) {
        testDeviceResult.setReRunParentId(parentTestDeviceResult.getId());
        fetchTestSuitesResultsReRunList(parentTestDeviceResult.getId());
      } else {
        log.info("Execution Environment (" + testDevice.getId() + ") is not eligible for Re-run. Skipping...");
        return null;
      }
    }

    testDeviceResult.setTestPlanResultId(testPlanResult.getId());
    testDeviceResult.setResult(ResultConstant.QUEUED);
    testDeviceResult.setStatus(StatusConstant.STATUS_CREATED);
    testDeviceResult.setMessage(AutomatorMessages.MSG_EXECUTION_CREATED);
    testDeviceResult.setStartTime(new Timestamp(System.currentTimeMillis()));
    testDeviceResult.setTestDeviceId(testDevice.getId());
    testDeviceResult.setTestDeviceSettings(getExecutionTestDeviceSettings(testDevice));
    testDeviceResult = testDeviceResultService.create(testDeviceResult);
    testDeviceResult.setTestDevice(testDevice);
    return testDeviceResult;
  }

  private TestPlanResult createTestPlanResult() throws ResourceNotFoundException {
    TestPlanResult testPlanResult = new TestPlanResult();
    if (getIsReRun()) {
      if (this.getParentTestPlanResultId() != null) {
        testPlanResult.setReRunParentId(this.getParentTestPlanResultId());
      } else {
        testPlanResult.setReRunParentId(testPlan.getLastRunId());
      }
      testPlanResult.setReRunType(getReRunType());
      fetchEnvironmentResultsReRunList();
    }
    if ((this.getRunTimeData() != null) && (this.getRunTimeData().has("build_number"))) {
      testPlanResult.setBuildNo(this.getRunTimeData().getString("build_number"));
    }
    testPlanResult.setResult(ResultConstant.QUEUED);
    testPlanResult.setStatus(StatusConstant.STATUS_CREATED);
    testPlanResult.setMessage(AutomatorMessages.MSG_EXECUTION_CREATED);
    testPlanResult.setTestPlanId(this.getTestPlan().getId());
    testPlanResult.setStartTime(new Timestamp(System.currentTimeMillis()));
    testPlanResult.setTriggeredType(this.triggeredType);
    testPlanResult.setScheduleId(this.scheduleId);

    TestPlanDetails testPlanDetails = new TestPlanDetails();
    testPlanDetails.setElementTimeout(testPlan.getElementTimeOut());
    testPlanDetails.setPageTimeout(testPlan.getPageTimeOut());
    testPlanDetails.setOnAbortedAction(testPlan.getOnAbortedAction());
    testPlanDetails.setRecoveryAction(testPlan.getRecoveryAction());
    testPlanDetails.setGroupPrerequisiteFail(testPlan.getOnSuitePreRequisiteFail());
    testPlanDetails.setTestCasePrerequisiteFail(testPlan.getOnTestcasePreRequisiteFail());
    testPlanDetails.setTestStepPrerequisiteFail(testPlan.getOnStepPreRequisiteFail());
    testPlanDetails.setScreenshotOption(testPlan.getScreenshot());

    if (this.getTestPlan().getEnvironmentId() != null) {
      Environment environment = environmentService.find(this.getTestPlan().getEnvironmentId());
      testPlanResult.setEnvironmentId(environment.getId());
      testPlanDetails.setEnvironmentParamName(environment.getName());
    }
    testPlanResult.setTestPlanDetails(testPlanDetails);
    return testPlanResultService.create(testPlanResult);
  }

  private void checkForDataDrivenIntegrity(TestCase testCase) throws TestsigmaException {
    TestData testData = testCase.getTestData();
    if (testData == null && testCase.getIsDataDriven()) {
      String errorMessage = com.testsigma.constants.MessageConstants.getMessage(
        MessageConstants.MSG_UNKNOWN_TEST_DATA_DATA_DRIVEN_CASE,
        testCase.getName()
      );
      throw new TestsigmaException(errorMessage);
    }
  }

  private TestDeviceSettings getExecutionTestDeviceSettings(TestDevice testDevice) throws TestsigmaException {
    TestDeviceSettings settings = new TestDeviceSettings();
    TestPlanLabType exeLabType = this.getTestPlan().getTestPlanLabType();

    if (testDevice.getPlatformDeviceId() != null) {
      settings.setDeviceName(platformsService.getPlatformDevice(testDevice.getPlatformDeviceId(), exeLabType).getName());
    }
    if (testDevice.getPlatformBrowserVersionId() != null) {
      PlatformBrowserVersion platformBrowserVersion = platformsService.getPlatformBrowserVersion(testDevice.getPlatformBrowserVersionId(), exeLabType);
      settings.setBrowserVersion(platformBrowserVersion.getVersion());
      settings.setBrowser(platformBrowserVersion.getName().name());
    }
    if (testDevice.getPlatformScreenResolutionId() != null) {
      settings.setResolution(platformsService.getPlatformScreenResolution(testDevice.getPlatformScreenResolutionId(), exeLabType).getResolution());
    }
    if (testDevice.getPlatformOsVersionId() != null) {
      PlatformOsVersion platformOsVersion = platformsService.getPlatformOsVersion(testDevice.getPlatformOsVersionId(), exeLabType);
      settings.setPlatform(platformOsVersion.getPlatform());
      settings.setOsVersion(platformOsVersion.getPlatformVersion());
    }
    if (exeLabType == TestPlanLabType.Hybrid) {
      settings.setBrowser(testDevice.getBrowser());
    }
    settings.setAppActivity(testDevice.getAppActivity());
    settings.setAppPackage(testDevice.getAppPackage());
    settings.setAppPathType(testDevice.getAppPathType());
    settings.setAppUrl(testDevice.getAppUrl());
    settings.setAppUploadId(testDevice.getAppUploadId());

    settings.setTitle(testDevice.getTitle());
    settings.setCreateSessionAtCaseLevel(testDevice.getCreateSessionAtCaseLevel());
    return settings;
  }


  private Boolean isScheduledExecution() {
    return this.triggeredType.equals(ExecutionTriggeredType.SCHEDULED);
  }

  // ############################################ RESULT ENTRIES PROCESSING ###########################################

  private void processResultEntries() throws Exception {
    if (canPushToLabAgent()) {
      processResultEntriesForLabAgent();
    } else if (canPushToHybridAgent()) {
      processResultEntriesForHybridAgent();
    }
  }

  private Boolean canPushToLabAgent() throws IntegrationNotFoundException {
    return !this.testPlan.getTestPlanLabType().equals(TestPlanLabType.Hybrid) && this.integrationsService.findByApplication(Integration.TestsigmaLab) != null;
  }

  private Boolean canPushToHybridAgent() {
    return this.testPlan.getTestPlanLabType().equals(TestPlanLabType.Hybrid);
  }

  private void processResultEntriesForLabAgent() throws Exception {
    List<TestDeviceResult> testDeviceResults = testDeviceResultService.findAllByTestPlanResultId(
      this.testPlanResult.getId());
    processResultEntries(testDeviceResults, StatusConstant.STATUS_CREATED);
  }

  private void processResultEntriesForHybridAgent() throws Exception {
    List<TestDeviceResult> testDeviceResults = testDeviceResultService.findAllByTestPlanResultId(
      this.testPlanResult.getId());
    processResultEntries(testDeviceResults, StatusConstant.STATUS_CREATED);
  }

  public void processResultEntries(List<TestDeviceResult> testDeviceResults, StatusConstant inStatus)
    throws Exception {
    for (TestDeviceResult testDeviceResult : testDeviceResults) {
      if (testDeviceResult.getTestDevice().getAgent() == null && this.getTestPlan().getTestPlanLabType().isHybrid()) {
        testDeviceResultService.markEnvironmentResultAsFailed(testDeviceResult, AutomatorMessages.AGENT_HAS_BEEN_REMOVED, StatusConstant.STATUS_CREATED);
      } else if (this.getTestPlan().getTestPlanLabType().isHybrid() && !agentService.isAgentActive(testDeviceResult.getTestDevice().getAgentId())) {
          testDeviceResultService.markEnvironmentResultAsFailed(testDeviceResult,
            AutomatorMessages.AGENT_INACTIVE, StatusConstant.STATUS_CREATED);
      } else if(this.getTestPlan().getTestPlanLabType().isHybrid() && testDeviceResult.getTestDevice().getDeviceId()!=null &&
        agentService.isAgentActive(testDeviceResult.getTestDevice().getAgentId()) && !agentDeviceService.isDeviceOnline(testDeviceResult.getTestDevice().getDeviceId())){
        testDeviceResultService.markEnvironmentResultAsFailed(testDeviceResult,
          agentDeviceService.find(testDeviceResult.getTestDevice().getDeviceId()).getName()+ " "+AutomatorMessages.DEVICE_NOT_ONLINE, StatusConstant.STATUS_CREATED);
      }
      else {
          processEnvironmentResult(testDeviceResult, inStatus);
      }
    }
    testDeviceResultService.updateExecutionConsolidatedResults(this.testPlanResult.getId(),
      Boolean.TRUE);
  }

  public void processEnvironmentResult(TestDeviceResult testDeviceResult, StatusConstant inStatus) throws Exception {
    testDeviceResultService.markEnvironmentResultAsInPreFlight(testDeviceResult, inStatus);
    if (!this.getTestPlan().getTestPlanLabType().isHybrid()) {
      EnvironmentEntityDTO environmentEntityDTO = loadEnvironment(testDeviceResult,
        StatusConstant.STATUS_PRE_FLIGHT);
      try {
        pushEnvironmentToLab(testDeviceResult, environmentEntityDTO);
      } catch (Exception e) {
        log.error(e.getMessage(), e);
        testDeviceResultService.markEnvironmentResultAsFailed(testDeviceResult, e.getMessage(),
          StatusConstant.STATUS_PRE_FLIGHT);
      }
    }
  }

  public void processEnvironmentResultInParallel(TestDeviceResult testDeviceResult, StatusConstant inStatus) throws Exception {
    List<TestSuiteResult> testSuiteResults = this.testSuiteResultService.findPendingTestSuiteResults(
      testDeviceResult, inStatus);
    testDeviceResult.setSuiteResults(testSuiteResults);
    for (TestSuiteResult testSuiteResult : testSuiteResults) {
      testSuiteResultService.markTestSuiteResultAsInFlight(testSuiteResult, inStatus);
      if (!this.getTestPlan().getTestPlanLabType().isHybrid()) {
        TestSuiteEntityDTO testSuiteEntity = this.testSuiteResultMapper.map(testSuiteResult);
        testSuiteEntity.setTestCases(loadTestCases(testSuiteResult, StatusConstant.STATUS_PRE_FLIGHT));
        List<TestSuiteEntityDTO> testSuiteEntityDTOS = new ArrayList<>();
        testSuiteEntityDTOS.add(testSuiteEntity);
        EnvironmentEntityDTO environmentEntityDTO = loadEnvironmentDetails(testDeviceResult);
        environmentEntityDTO.setTestSuites(testSuiteEntityDTOS);
        try {
          pushEnvironmentToLab(testDeviceResult, environmentEntityDTO);
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          testSuiteResultService.markTestSuiteResultAsFailed(testSuiteResult, e.getMessage(),
            StatusConstant.STATUS_PRE_FLIGHT);
        }
      }
    }
    testDeviceResultService.updateEnvironmentConsolidatedResults(testDeviceResult);
  }

  public EnvironmentEntityDTO loadEnvironment(TestDeviceResult testDeviceResult, StatusConstant inStatus)
    throws Exception {
    List<TestSuiteEntityDTO> testSuiteEntityDTOS = loadTestSuites(testDeviceResult, inStatus);
    EnvironmentEntityDTO environmentEntityDTO = loadEnvironmentDetails(testDeviceResult);
    environmentEntityDTO.setTestSuites(testSuiteEntityDTOS);
    return environmentEntityDTO;
  }

  public EnvironmentEntityDTO loadEnvironmentDetails(TestDeviceResult testDeviceResult) throws Exception {
    TestPlanSettingEntityDTO testPlanSettingEntityDTO = this.testPlanMapper.mapSettings(this.testPlan);
    EnvironmentEntityDTO environmentEntityDTO = this.testDeviceResultMapper.map(testDeviceResult);
    TestDevice testDevice = testDeviceResult.getTestDevice();
    if (testDevice.getDeviceId() != null) {
      AgentDevice agentDevice = agentDeviceService.find(testDevice.getDeviceId());
      environmentEntityDTO.setAgentDeviceUuid(agentDevice.getUniqueId());
    }
    environmentEntityDTO.setStorageType(storageServiceFactory.getStorageService().getStorageType());
    environmentEntityDTO.setWorkspaceType(this.getAppType());
    environmentEntityDTO.setTestPlanSettings(testPlanSettingEntityDTO);
    environmentEntityDTO.setMatchBrowserVersion(testDevice.getMatchBrowserVersion());
    environmentEntityDTO.setCreateSessionAtCaseLevel(testDevice.getCreateSessionAtCaseLevel());
    TestDeviceSettings settings = getExecutionTestDeviceSettings(testDevice);
    settings.setExecutionName(testPlan.getName());
    settings.setEnvironmentParamId(this.testPlan.getEnvironmentId());
    settings.setEnvRunId(testDeviceResult.getId());
    setTestLabDetails(testDevice, settings,environmentEntityDTO);
    environmentEntityDTO.setEnvSettings(this.testPlanMapper.mapToDTO(settings));
    Agent agent = null;
    if (testDevice.getAgentId() != null)
      agent = this.agentService.find(testDevice.getAgentId());
    setAgentJWTApiKey(environmentEntityDTO, agent);
    setAvailableFeatures(environmentEntityDTO);
    return environmentEntityDTO;
  }

  private List<TestSuiteEntityDTO> loadTestSuites(TestDeviceResult testDeviceResult, StatusConstant inStatus) {
    List<TestSuiteEntityDTO> testSuiteEntityDTOS = new ArrayList<>();
    List<TestSuiteResult> testSuiteResults = this.testSuiteResultService.findPendingTestSuiteResults(testDeviceResult,
      inStatus);

    for (TestSuiteResult testSuiteResult : testSuiteResults) {
      TestSuiteEntityDTO testSuiteEntity = this.testSuiteResultMapper.map(testSuiteResult);
      testSuiteEntity.setTestCases(loadTestCases(testSuiteResult, inStatus));
      testSuiteEntityDTOS.add(testSuiteEntity);
    }
    return testSuiteEntityDTOS;
  }

  private List<TestCaseEntityDTO> loadTestCases(TestSuiteResult testSuiteResult, StatusConstant inStatus) {
    List<TestCaseResult> testCaseResults = this.testCaseResultService.findActiveSuiteTestCaseResults(
      testSuiteResult.getId(), inStatus);
    List<TestCaseEntityDTO> testCases = new ArrayList<>();

    for (TestCaseResult testCaseResult : testCaseResults) {
      TestCaseEntityDTO testCaseEntity = this.testCaseResultMapper.map(testCaseResult);
      testCaseEntity.setDataDrivenTestCases(loadDataDrivenTestCases(testCaseResult, inStatus));
      testCases.add(testCaseEntity);
    }
    return testCases;
  }

  private List<TestCaseEntityDTO> loadDataDrivenTestCases(TestCaseResult testCaseResult, StatusConstant inStatus) {
    List<TestCaseResult> dataDrivenTestCaseResults =
      this.testCaseResultService.findAllByParentIdAndStatus(testCaseResult.getId(), inStatus);
    return this.testCaseResultMapper.map(dataDrivenTestCaseResults);
  }

  private void setAgentJWTApiKey(EnvironmentEntityDTO environmentEntityDTO, com.testsigma.model.Agent id) throws ResourceNotFoundException {
    TestDeviceSettingsDTO envSettings = environmentEntityDTO.getEnvSettings();
    if (id != null) {
      Agent agent = this.agentService.find(id.getId());
      envSettings.setJwtApiKey(agent.generateJwtApiKey(jwtTokenService.getServerUuid()));
    }
    environmentEntityDTO.setEnvSettings(envSettings);
  }

  private void setAvailableFeatures(EnvironmentEntityDTO dto) throws ResourceNotFoundException, SQLException {
    dto.getTestPlanSettings().setHasSuggestionFeature(true);
  }

  private void pushEnvironmentToLab(TestDeviceResult testDeviceResult, EnvironmentEntityDTO environmentEntityDTO) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    TestDeviceEntity testDeviceEntity = objectMapper.readValue(objectMapper.writeValueAsString(environmentEntityDTO),
      TestDeviceEntity.class);
    try {
      testDeviceResultService.markEnvironmentResultAsInProgress(testDeviceResult, StatusConstant.STATUS_PRE_FLIGHT,
        Boolean.TRUE);
      new TestPlanRunTask(testDeviceEntity).start();
      log.info("Successfully pushed Execution Environment[" + testDeviceEntity.getEnvironmentResultId()
        + "] to Testsigma Lab");
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      String message = "Error while pushing environment to agent - " + e.getMessage();
      throw new TestsigmaException(message, message);
    }
  }

  private void saveRunTimeData() {
    JSONObject runtimeJSONObj = this.getRunTimeData();
    if (runtimeJSONObj != null && runtimeJSONObj.has("runtime_data")) {
      RunTimeData runTimeData = new RunTimeData();
      runTimeData.setTestPlanRunId(this.getTestPlanResult().getId());
      if (runtimeJSONObj.has("runtime_data")) {
        runTimeData.setData(runtimeJSONObj.getJSONObject("runtime_data"));
      }
      this.runTimeDataService.create(runTimeData);
    }
  }

  public void checkTestCaseIsInReadyState(TestCase testCase) throws TestsigmaException {
    if (!testCase.getStatus().equals(TestCaseStatus.READY) && testPlan.getEntityType()=="TEST_PLAN") {
      String message = testCase.getIsStepGroup() ? com.testsigma.constants.MessageConstants.getMessage(MessageConstants.STEP_GROUP_NOT_READY, testCase.getName()) :
        MessageConstants.TESTCASE_NOT_READY;
      throw new TestsigmaException(message, message);
    }
  }

  private void fetchEnvironmentResultsReRunList() {
    testDeviceResultsReRunList = new ArrayList<>();
    if (getReRunType() == ReRunType.ALL_TESTS) {
      testDeviceResultsReRunList = testDeviceResultService.findAllByTestPlanResultId(this.getParentTestPlanResultId());
    } else if (getReRunType() == ReRunType.ONLY_FAILED_TESTS) {
      testDeviceResultsReRunList = testDeviceResultService.findAllByTestPlanResultIdAndResultIsNot
        (this.getParentTestPlanResultId(), ResultConstant.SUCCESS);
    }
  }

  private void fetchTestSuitesResultsReRunList(Long parentEnvironmentResultId) {
    testSuiteResultsReRunList = new ArrayList<>();
    try {
      TestDeviceResult parentTestDeviceResult = testDeviceResultService.find(parentEnvironmentResultId);
      List<TestSuiteResult> failedTestSuites;
      if (parentTestDeviceResult != null) {
        if (getReRunType() == ReRunType.ALL_TESTS) {
          testSuiteResultsReRunList = testSuiteResultService.findAllByEnvironmentResultId(parentTestDeviceResult.getId());
        } else if (getReRunType() == ReRunType.ONLY_FAILED_TESTS) {
          failedTestSuites = testSuiteResultService.findAllByEnvironmentResultIdAndResultIsNot
            (parentTestDeviceResult.getId(), ResultConstant.SUCCESS);
          if (failedTestSuites.size() > 0) {
            for (TestSuiteResult testSuiteResult : failedTestSuites) {
              List<Long> testSuitePreRequisiteIds = findTestSuitePreRequisiteIds(testSuiteResult, new ArrayList<>(), 0);
              if (testSuitePreRequisiteIds.size() > 0) {
                List<TestSuiteResult> preRequisiteResults = testSuiteResultService.findBySuiteResultIds(testSuitePreRequisiteIds);
                testSuiteResultsReRunList.addAll(preRequisiteResults);
              }
              testSuiteResultsReRunList.add(testSuiteResult);
            }
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private List<Long> findTestSuitePreRequisiteIds(TestSuiteResult testSuiteResult, List<Long> testSuitePreRequisiteIds,
                                                  int depth) {
    if (depth < PRE_REQUISITE_DEPTH) {
      TestSuiteResult preReqTestSuiteResult;
      try {
        TestSuite testSuite = testSuiteResult.getTestSuite();
        if (testSuite.getPreRequisite() != null) {
          preReqTestSuiteResult = testSuiteResultService.findByEnvironmentResultIdAndSuiteId(
            testSuiteResult.getEnvironmentResultId(), testSuite.getPreRequisite());
          if (preReqTestSuiteResult != null) {
            testSuitePreRequisiteIds = findTestSuitePreRequisiteIds(preReqTestSuiteResult, testSuitePreRequisiteIds,
              depth + 1);
            testSuitePreRequisiteIds.add(preReqTestSuiteResult.getId());
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    return testSuitePreRequisiteIds;
  }

  private void fetchTestCaseResultsReRunList(Long parentTestSuiteResultId) {
    testCaseResultsReRunList = new ArrayList<>();
    try {
      TestSuiteResult parentTestSuiteResult = testSuiteResultService.find(parentTestSuiteResultId);
      List<TestCaseResult> failedTestCases;
      if (parentTestSuiteResult != null) {
        if (getReRunType() == ReRunType.ALL_TESTS || isTestSuiteAPrerequisite(parentTestSuiteResult)) {
          testCaseResultsReRunList = testCaseResultService.findAllBySuiteResultId(parentTestSuiteResult.getId());
        } else if (getReRunType() == ReRunType.ONLY_FAILED_TESTS) {
          failedTestCases = testCaseResultService.findAllBySuiteResultIdAndResultIsNot
            (parentTestSuiteResult.getId(), ResultConstant.SUCCESS);
          if (failedTestCases.size() > 0) {
            for (TestCaseResult testCaseResult : failedTestCases) {
              List<Long> testCasePreRequisiteIds = findTestCasePreRequisiteIds(testCaseResult, new ArrayList<>(), 0);
              //If a prerequisite is failed, it will be already available in failedTestCases. So we need to add only prerequisites with SUCCESS status.
              List<TestCaseResult> preRequisiteResults = fetchPreRequisiteTestCaseResultsWithSuccessStatus(testCasePreRequisiteIds);
              testCaseResultsReRunList.addAll(preRequisiteResults);
              testCaseResultsReRunList.add(testCaseResult);
            }
          }
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private List<TestCaseResult> fetchPreRequisiteTestCaseResultsWithSuccessStatus(List<Long> testCasePreRequisiteIds) {
    List<TestCaseResult> preRequisitesWithSuccessStatus = new ArrayList<>();
    List<TestCaseResult> preRequisiteResults = testCaseResultService.findByTestCaseResultIds(testCasePreRequisiteIds);
    for (TestCaseResult testCaseResult : preRequisiteResults) {
      if (testCaseResult.getResult() == ResultConstant.SUCCESS) {
        preRequisitesWithSuccessStatus.add(testCaseResult);
      }
    }
    return preRequisitesWithSuccessStatus;
  }

  private boolean isTestSuiteAPrerequisite(TestSuiteResult testSuiteResult) {
    List<TestSuite> testSuites = testSuiteService.findByPrerequisiteId(testSuiteResult.getSuiteId());
    for (TestSuite testSuite : testSuites) {
      TestSuiteResult baseTestSuiteResult = testSuiteResultService.findByEnvironmentResultIdAndSuiteId(testSuiteResult.getEnvironmentResultId(), testSuite.getId());
      if (baseTestSuiteResult != null) {
        return true;
      }
    }
    return false;
  }

  private List<Long> findTestCasePreRequisiteIds(TestCaseResult testCaseResult, List<Long> testCasePreRequisiteIds,
                                                 int depth) {
    if (depth < PRE_REQUISITE_DEPTH) {
      List<TestCaseResult> preReqTestCaseResults;
      try {
        TestCase testCase = testCaseResult.getTestCase();
        if (testCase.getPreRequisite() != null) {
          //In case of data-driven tests, we have multiple rows in TestCaseResult table for each dataset(each row in testdata profile)
          preReqTestCaseResults = testCaseResultService.findAllBySuiteResultIdAndTestCaseId(
            testCaseResult.getSuiteResultId(), testCase.getPreRequisite());
          if (preReqTestCaseResults != null) {
            for (TestCaseResult preReqTestCaseResult : preReqTestCaseResults) {
              testCasePreRequisiteIds = findTestCasePreRequisiteIds(preReqTestCaseResult, testCasePreRequisiteIds,
                depth + 1);
              testCasePreRequisiteIds.add(preReqTestCaseResult.getId());
            }
          }
        }
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    }
    return testCasePreRequisiteIds;
  }

  // ################################################ AFTER START  ###################################################

  private void afterStart() throws Exception {
    setInitialCounts();
  }

  private void setInitialCounts() {
    List<TestDeviceResult> executionEnvironmentsResults
      = testDeviceResultService.findAllByTestPlanResultId(this.getTestPlanResult().getId());

    for (TestDeviceResult executionTestDeviceResult : executionEnvironmentsResults) {
      List<TestCaseResult> testCaseResults = testCaseResultService.findAllByEnvironmentResultId(executionTestDeviceResult.getId());
      for (TestCaseResult testCaseResult : testCaseResults) {
        testCaseResultService.updateResultCounts(testCaseResult);
      }
      List<TestSuiteResult> testSuitesResults = testSuiteResultService.findAllByEnvironmentResultId(executionTestDeviceResult.getId());
      for (TestSuiteResult testSuiteResult : testSuitesResults) {
        testSuiteResultService.updateResultCounts(testSuiteResult.getId());
      }
      testDeviceResultService.updateResultCounts(executionTestDeviceResult.getId());
    }
  }

  // ################################################  STOP  ###################################################

  public void stop() throws Exception {
    beforeStop();
    stopQueuedEnvironments(AutomatorMessages.MSG_USER_ABORTED_EXECUTION, Boolean.TRUE);
    afterStop();
  }

  private void beforeStop() throws TestsigmaException {
    TestPlanResult testPlanResult = this.testPlanResultService.findByTestPlanIdAndStatusIsNot(this.testPlan.getId(),
      StatusConstant.STATUS_COMPLETED);
    if (testPlanResult == null) {
      throw new TestsigmaException("No Queued executions for test plan - " + this.getTestPlan().getName());
    }
    this.setTestPlanResult(testPlanResult);
  }

  private void stopQueuedEnvironments(String errorMessage, Boolean sendPendingExecutions) {
    List<TestDeviceResult> testDeviceResults = this.testDeviceResultService
      .findAllByTestPlanResultIdAndStatusIsNot(this.testPlanResult.getId(), StatusConstant.STATUS_COMPLETED);
    for (TestDeviceResult testDeviceResult : testDeviceResults) {
      testDeviceResultService.markEnvironmentResultAsStopped(testDeviceResult, errorMessage);
      testDeviceResultService.updateResultCounts(testDeviceResult.getId());
    }
    Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());

    TestPlanResult testPlanResult = this.testPlanResult;
    testPlanResult.setResult(ResultConstant.STOPPED);
    testPlanResult.setStatus(StatusConstant.STATUS_COMPLETED);
    testPlanResult.setMessage(errorMessage);
    testPlanResult.setEndTime(currentTime);
    testPlanResult.setDuration(testPlanResult.getEndTime().getTime() - testPlanResult.getStartTime().getTime());
    this.testPlanResultService.update(testPlanResult);

    try {
      if (sendPendingExecutions) {
        testDeviceResultService.sendPendingTestPlans();
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  private void afterStop() {
  }

  protected void setTestLabDetails(TestDevice testDevice, TestDeviceSettings settings,EnvironmentEntityDTO environmentEntityDTO)
    throws Exception {
    if (this.testPlan.getWorkspaceVersion().getWorkspace().getWorkspaceType().isRest())
      return;
    TestPlanLabType exeLabType = this.getTestPlan().getTestPlanLabType();
    setPlatformDetails(testDevice, settings, exeLabType, testDevice.getAgent(), environmentEntityDTO);
  }

  public void loadTestCase(String testDataSetName, TestCaseEntityDTO testCaseEntityDTO, AbstractTestPlan testPlan,
                           Workspace workspace) throws Exception {

    String profileName = null;
    String environmentProfileName = null;
    Map<String, String> environmentParameters = null;
    List<com.testsigma.model.TestDataSet> databank = new ArrayList<>();
    com.testsigma.model.TestDataSet dataSet = null;

    if (testPlan.getEnvironmentId() != null) {
      Environment environment = testPlan.getEnvironment();
      environmentParameters = environment.getData();
      environmentProfileName = environment.getName();
    }

    List<TestStep> testSteps = testStepService.findAllByTestCaseIdAndEnabled(testCaseEntityDTO.getId());


    List<TestStepDTO> testStepDTOS = testStepMapper.mapDTOs(testSteps);

    Long testDataId = testCaseEntityDTO.getTestDataId();
    if (testDataId != null) {
      TestData testData = testDataProfileService.find(testCaseEntityDTO.getTestDataId());
      databank = testData.getData();
      profileName = testData.getTestDataName();
    }

    List<Long> testCaseIds = new ArrayList<>();
    testCaseIds.add(testCaseEntityDTO.getId());
    for (TestStepDTO testStepDTO : testStepDTOS) {
      if (testStepDTO.getStepGroupId() != null) {
        TestCase testCase = testCaseService.find(testStepDTO.getStepGroupId());
        checkTestCaseIsInReadyState(testCase);
        List<TestStep> childSteps;
        childSteps = testStepService.findAllByTestCaseIdAndEnabled(testStepDTO.getStepGroupId());

        List<TestStepDTO> childStepsDTOs = testStepMapper.mapDTOs(childSteps);
        testStepDTO.setTestStepDTOS(childStepsDTOs);
        testCaseIds.add(testStepDTO.getStepGroupId());
      }
      if (testStepDTO.getAddonActionId() != null) {
        if (!this.getTestPlan().getTestPlanLabType().isHybrid()) {
          AddonNaturalTextAction addonNaturalTextAction = addonNaturalTextActionService.findById(testStepDTO.getAddonActionId());
          Addon addon = addonService.findById(addonNaturalTextAction.getAddonId());
          if (addon.getStatus() == AddonStatus.DRAFT) {
            throw new TestsigmaException(MessageConstants.DRAFT_PLUGIN_ALLOWED_IN_HYBRID_ONLY,
              MessageConstants.DRAFT_PLUGIN_ALLOWED_IN_HYBRID_ONLY);
          }
        }
      }
    }

    List<String> elementNames = testStepService.findElementNamesByTestCaseIds(testCaseIds);
    elementNames.addAll(testStepService.findAddonActionElementsByTestCaseIds(testCaseIds));

    List<Element> elementList = elementService.findByNameInAndWorkspaceVersionId(elementNames,
      testPlan.getWorkspaceVersionId());
    Map<String, Element> elements = new HashMap<>();
    for (Element element : elementList) {
      elements.put(element.getName().toLowerCase(), element);
    }

    if (!testCaseEntityDTO.getIsDataDriven()) {
      if (!databank.isEmpty()) {
        dataSet = databank.get(testCaseEntityDTO.getTestDataStartIndex());
        testCaseEntityDTO.setTestDataSetName(dataSet.getName());
      }
    } else {
      for (TestDataSet data : databank) {
        if (data.getName().equals(testDataSetName)) {
          dataSet = data;
          break;
        }
      }
      testCaseEntityDTO.setTestDataSetName(dataSet.getName());
      testCaseEntityDTO.setExpectedToFail(dataSet.getExpectedToFail());
    }

    List<TestCaseStepEntityDTO> executableList = getExecutableTestSteps(
      workspace.getWorkspaceType(), testStepDTOS,
      elements, dataSet, testPlan.getId(),
      environmentParameters, testCaseEntityDTO, environmentProfileName,
      profileName);
    appendPreSignedURLs(executableList, testCaseEntityDTO, false, null, null);

    testCaseEntityDTO.setTestSteps(executableList);

    TestCaseResult testCaseResult = testCaseResultService.find(testCaseEntityDTO.getTestCaseResultId());
    testCaseResultService.markTestCaseResultAsInProgress(testCaseResult);
  }

  private boolean isStepInsideForLoop(TestCaseStepEntityDTO testCaseStepEntity) throws ResourceNotFoundException {
    if (testCaseStepEntity.getParentId() != null) {
      TestStep testStep = testStepService.find(testCaseStepEntity.getParentId());
      return (testStep.getType() == TestStepType.FOR_LOOP);
    }
    return false;
  }

  protected void appendPreSignedURLs(List<TestCaseStepEntityDTO> executableList, TestCaseEntityDTO testCaseEntity,
                                       boolean isWhileLoop, Long stepGroupStepID, TestCaseStepEntityDTO parentGroupEntity)
    throws ResourceNotFoundException {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, 10);
    stepGroupStepID = (stepGroupStepID == null) ? 0 : stepGroupStepID;
    StorageService storageService = this.storageServiceFactory.getStorageService();
    for (TestCaseStepEntityDTO testCaseStepEntity : executableList) {
      Integer index;
      if (parentGroupEntity != null && !isStepInsideForLoop(testCaseStepEntity)) {
        index = (parentGroupEntity.getIndex() == null) ? 0 : parentGroupEntity.getIndex();
      } else {
        index = (testCaseStepEntity.getIndex() == null) ? 0 : testCaseStepEntity.getIndex();
      }
      String screenShotPath = String.format("/executions/%s/%s_%s_%s_%s.%s", testCaseEntity.getTestCaseResultId(),
        testCaseStepEntity.getId(), stepGroupStepID, testCaseStepEntity.getPosition(), index, "jpeg");

      URL presignedURL = storageService.generatePreSignedURL(screenShotPath, StorageAccessLevel.WRITE, 600);
      testCaseStepEntity.setScreenshotPath(presignedURL.toString());
      handleUploadActionStep(testCaseStepEntity,storageService);
      handleInstallApp(testCaseStepEntity,storageService);
      if ((testCaseStepEntity.getTestCaseSteps() != null) && !testCaseStepEntity.getTestCaseSteps().isEmpty()) {
        if (testCaseStepEntity.getConditionType() == TestStepConditionType.LOOP_WHILE) {
          addScreenshotPresignedURLsForWhileLoop(testCaseStepEntity, testCaseEntity, stepGroupStepID, parentGroupEntity,storageService);
          appendPreSignedURLs(testCaseStepEntity.getTestCaseSteps(), testCaseEntity, true, stepGroupStepID, parentGroupEntity);
        } else if (testCaseStepEntity.getType() == TestStepType.STEP_GROUP) {
          Long parentGroupStepId = (stepGroupStepID != 0) ? stepGroupStepID : testCaseStepEntity.getId();
          appendPreSignedURLs(testCaseStepEntity.getTestCaseSteps(), testCaseEntity, isWhileLoop, parentGroupStepId, testCaseStepEntity);
        } else {
          appendPreSignedURLs(testCaseStepEntity.getTestCaseSteps(), testCaseEntity, isWhileLoop, stepGroupStepID, parentGroupEntity);
        }
      }
      if (isWhileLoop && !(testCaseStepEntity.getType() == TestStepType.STEP_GROUP)) {
        addScreenshotPresignedURLsForWhileLoop(testCaseStepEntity, testCaseEntity, stepGroupStepID, parentGroupEntity,storageService);
      }
    }
  }

  private void addScreenshotPresignedURLsForWhileLoop(TestCaseStepEntityDTO testCaseStep, TestCaseEntityDTO testCaseEntity,
                                                      Long parentGroupStepId, TestCaseStepEntityDTO parentGroupEntity, StorageService storageService) {
    parentGroupStepId = (parentGroupStepId == null) ? 0 : parentGroupStepId;
    Map<String, String> additionalScreenshotPaths = new HashMap<>();
    for (int iteration = 1; iteration <= NaturalTextActionConstants.WHILE_LOOP_MAX_LIMIT; iteration++) {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.HOUR, 10);
      Integer index = null;
      if (parentGroupEntity != null) {
        index = (parentGroupEntity.getIndex() == null) ? 0 : parentGroupEntity.getIndex();
      } else {
        index = (testCaseStep.getIndex() == null) ? 0 : testCaseStep.getIndex();
      }
      String screenShotPath = String.format("/executions/%s/%s_%s_%s_%s_%s.%s", testCaseEntity.getTestCaseResultId(),
        testCaseStep.getId(), parentGroupStepId, iteration, testCaseStep.getPosition(), index, "jpeg");
      URL preSignedURL = storageService.generatePreSignedURL(screenShotPath, StorageAccessLevel.WRITE, 600);
      String iterationKey = String.format("%s_%s", iteration, testCaseStep.getIndex());
      additionalScreenshotPaths.put(iterationKey, preSignedURL.toString());
    }
    testCaseStep.setAdditionalScreenshotPaths(additionalScreenshotPaths);
  }

  private void handleUploadActionStep(TestCaseStepEntityDTO testCaseStepEntity, StorageService storageService) {
    if (testCaseStepEntity.getAction() != null && testCaseStepEntity.getAction().toLowerCase().contains("upload")
      && testCaseStepEntity.getNaturalTextActionId() != null && (testCaseStepEntity.getNaturalTextActionId().equals(969)
      || testCaseStepEntity.getNaturalTextActionId().equals(10150))) {
      handleFileActionStep(testCaseStepEntity,storageService);
    }
  }

  private void handleInstallApp(TestCaseStepEntityDTO testCaseStepEntity, StorageService storageService) {
    if (testCaseStepEntity.getAction() != null && testCaseStepEntity.getAction()
      .toLowerCase().contains("installApp".toLowerCase()) && (testCaseStepEntity.getNaturalTextActionId() != null)
      && (testCaseStepEntity.getNaturalTextActionId().equals(20003) || testCaseStepEntity.getNaturalTextActionId().equals(30003))) {
      handleFileActionStep(testCaseStepEntity,storageService);
    }
  }

  private void handleFileActionStep(TestCaseStepEntityDTO testCaseStepEntity, StorageService storageService) {
    com.testsigma.automator.entity.TestDataPropertiesEntity testDataPropertiesEntity = testCaseStepEntity.getTestDataMap().get(
      testCaseStepEntity.getTestDataMap().keySet().stream().findFirst().get());
    String fileUrl = testDataPropertiesEntity.getTestDataValue().replace("testsigma-storage://", "");
    URL newUrl = storageService.generatePreSignedURL(fileUrl, StorageAccessLevel.READ, 180);
    if(TestPlanLabType.TestsigmaLab == this.getTestPlan().getTestPlanLabType()) {
      try {
        newUrl = new URL(newUrl.toString().replace(applicationConfig.getServerUrl(), applicationConfig.getServerLocalUrl()));
      } catch (MalformedURLException ignore) {}
    }
    testDataPropertiesEntity.setTestDataValuePreSignedURL(newUrl.toString());
  }

  private void setPlatformDetails(TestDevice testDevice, TestDeviceSettings settings,
                                  TestPlanLabType testPlanLabType, Agent agent,EnvironmentEntityDTO environmentEntityDTO) throws TestsigmaException {

    populatePlatformOsDetails(testDevice, settings, testPlanLabType, agent);

    if (this.getAppType().isWeb()) {
      populatePlatformBrowserDetails(testDevice, settings, testPlanLabType, agent,environmentEntityDTO);
    }
  }

  protected void populatePlatformOsDetails(TestDevice testDevice, TestDeviceSettings settings,
                                           TestPlanLabType testPlanLabType, Agent agent)
    throws TestsigmaException {
    PlatformOsVersion platformOsVersion = null;

    if (testPlanLabType == TestPlanLabType.Hybrid) {
      Platform platform = null;
      String osVersion = null;
      if ((this.getAppType().isWeb()) && agent != null) {
        platform = agent.getOsType().getPlatform();
        osVersion = agent.getPlatformOsVersion(agent.getOsType().getPlatform());
      } else if (this.getAppType().isMobile() && testDevice.getDeviceId() != null) {
        AgentDevice agentDevice = this.agentDeviceService.find(testDevice.getDeviceId());
        osVersion = agentDevice.getPlatformOsVersion();
        platform = agentDevice.getOsName().getPlatform();
      }
      platformOsVersion = platformsService.getPlatformOsVersion(platform, osVersion, this.getAppType(), testPlanLabType);
    } else {
      platformOsVersion = platformsService.getPlatformOsVersion(testDevice.getPlatformOsVersionId(), testPlanLabType);
    }

    settings.setPlatform(platformOsVersion.getPlatform());
    if (TestPlanLabType.Hybrid == testPlanLabType) {
      settings.setOsVersion(platformOsVersion.getVersion());
    }
  }

  protected void populatePlatformBrowserDetails(TestDevice testDevice, TestDeviceSettings settings,
                                                TestPlanLabType testPlanLabType, Agent agent,EnvironmentEntityDTO environmentEntityDTO)
    throws TestsigmaException {


    PlatformBrowserVersion platformBrowserVersion = null;
    if (agent != null && testPlanLabType == TestPlanLabType.Hybrid) {
      Platform platform = agent.getOsType().getPlatform();
      String osVersion = agent.getPlatformOsVersion(platform);
      Browsers browser = OSBrowserType.getBrowser(testDevice.getBrowser());
      String browserVersion = agent.getBrowserVersion(browser.toString());
      platformBrowserVersion = platformsService.getPlatformBrowserVersion(platform, osVersion, browser, browserVersion, testPlanLabType);
    } else {
      platformBrowserVersion = platformsService.getPlatformBrowserVersion(testDevice.getPlatformBrowserVersionId(), testPlanLabType);
    }
    if (testPlanLabType.isHybrid()) {
      matchHybridBrowserVersion(agent, platformBrowserVersion, testDevice, platformBrowserVersion.getName(),environmentEntityDTO);
    }
    settings.setBrowser(platformBrowserVersion.getName().name());

    if (testPlanLabType == TestPlanLabType.Hybrid) {
      settings.setBrowserVersion(platformBrowserVersion.getVersion());
      settings.setHybridBrowserDriverPath(
        platformsService.getDriverPath(platformBrowserVersion.getPlatform(),
          platformBrowserVersion.getVersion(), platformBrowserVersion.getName(),
          platformBrowserVersion.getDriverVersion()));
    } else {
      settings.setBrowserVersion(platformBrowserVersion.getVersion());
    }
  }

  private void matchHybridBrowserVersion(Agent agent1, PlatformBrowserVersion platformBrowserVersion,
                                         TestDevice testDevice, Browsers browser,EnvironmentEntityDTO environmentEntityDTO)
    throws TestsigmaException {
    if ((agent1 != null) && (platformBrowserVersion != null)) {
      Agent agent = agentService.find(agent1.getId());
      for (AgentBrowser agentBrowser : agent.getBrowserList()) {
        Browsers aBrowser = Browsers.getBrowser(agentBrowser.getName().getBrowserName());
        if ((browser == aBrowser) &&
          (Boolean.TRUE == testDevice.getMatchBrowserVersion()) &&
          !platformBrowserVersion.getVersion().equals("" + agentBrowser.getMajorVersion())) {
          environmentEntityDTO.setErrorCode(ExceptionErrorCodes.BROWSER_VERSION_NOT_AVAILABLE);
          log.info("Local agent browser version[" + agentBrowser.getMajorVersion()
            + "] doesn't match selected browser version[" + platformBrowserVersion.getVersion() + "]");
        }
      }
    }
  }


  public List<TestCaseStepEntityDTO> getExecutableTestSteps(WorkspaceType workspaceType,
                                                            List<TestStepDTO> testStepDTOS,
                                                            Map<String, Element> elementMap,
                                                            com.testsigma.model.TestDataSet testDataSet,
                                                            Long testPlanId, Map<String, String> environmentParams,
                                                            TestCaseEntityDTO testCaseEntityDTO, String environmentParamSetName,
                                                            String dataProfile) throws Exception {

    List<Long> loopIds = new ArrayList<>();
    List<TestCaseStepEntityDTO> toReturn = new ArrayList<>();
    for (TestStepDTO testStepDTO : testStepDTOS) {

      if (loopIds.contains(testStepDTO.getParentId())) {
        continue;
      }

      if (testStepDTO.getType() == TestStepType.FOR_LOOP) {
        loopIds.add(testStepDTO.getId());
        new ForLoopStepProcessor(webApplicationContext, toReturn, workspaceType, elementMap, testStepDTO,
          testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile).processLoop(testStepDTOS, loopIds);
        continue;
      } else if (testStepDTO.getType() == TestStepType.WHILE_LOOP) {
        loopIds.add(testStepDTO.getId());
        new WhileLoopStepProcessor(webApplicationContext, toReturn, workspaceType, elementMap, testStepDTO,
          testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile).processWhileLoop(testStepDTOS, loopIds);
        continue;
      } else if (testStepDTO.getType() == TestStepType.REST_STEP) {
        new RestStepProcessor(webApplicationContext, toReturn, workspaceType, elementMap, testStepDTO,
          testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile).process();
        continue;
      }

      TestCaseStepEntityDTO stepEntity = new StepProcessor(webApplicationContext, toReturn, workspaceType,
        elementMap, testStepDTO, testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName,
        dataProfile).processStep();

      stepEntity.setStepGroupId(testStepDTO.getStepGroupId());
      stepEntity.setParentId(testStepDTO.getParentId());
      stepEntity.setConditionType(testStepDTO.getConditionType());

      if (testStepDTO.getTestStepDTOS() != null) {
        for (TestStepDTO subTestStepDTO : testStepDTO.getTestStepDTOS()) {
          List<TestCaseStepEntityDTO> stepGroupSpecialSteps = new ArrayList<>();
          if (stepEntity.getTestCaseSteps() == null) {
            stepEntity.setTestCaseSteps(new ArrayList<>());
          }
          //TODO: check logic for test step key Generation and recursive logic for step group generation
          if (loopIds.contains(subTestStepDTO.getParentId())) {
            continue;
          }

          if (subTestStepDTO.getType() == TestStepType.FOR_LOOP) {
            loopIds.add(subTestStepDTO.getId());
            new ForLoopStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType, elementMap, subTestStepDTO,
              testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile)
              .processLoop(testStepDTO.getTestStepDTOS(), loopIds);
            stepEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
            continue;
          } else if (subTestStepDTO.getType() == TestStepType.WHILE_LOOP) {
            loopIds.add(subTestStepDTO.getId());
            new WhileLoopStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType, elementMap,
              subTestStepDTO, testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile)
              .processWhileLoop(testStepDTO.getTestStepDTOS(), loopIds);
            stepEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
            continue;
          } else if (subTestStepDTO.getType() == TestStepType.REST_STEP) {
            new RestStepProcessor(webApplicationContext, stepGroupSpecialSteps, workspaceType, elementMap, subTestStepDTO,
              testPlanId, testDataSet, environmentParams, testCaseEntityDTO, environmentParamSetName, dataProfile).process();
            stepEntity.getTestCaseSteps().addAll(stepGroupSpecialSteps);
            continue;
          }

          TestCaseStepEntityDTO cstepEntity = new StepProcessor(webApplicationContext, toReturn, workspaceType,
            elementMap, subTestStepDTO, testPlanId, testDataSet, environmentParams, testCaseEntityDTO,
            environmentParamSetName, dataProfile).processStep();

          cstepEntity.setParentId(subTestStepDTO.getParentId());
          cstepEntity.setConditionType(subTestStepDTO.getConditionType());
          stepEntity.getTestCaseSteps().add(cstepEntity);
        }
      }
      toReturn.add(stepEntity);
    }

    return toReturn;
  }

  private WorkspaceType getAppType() {
    return this.testPlan.getWorkspaceVersion().getWorkspace().getWorkspaceType();
  }

}
