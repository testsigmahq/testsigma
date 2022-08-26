/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.constants.AutomatorMessages;
import com.testsigma.dto.EnvironmentEntityDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestDeviceResultMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestDeviceResultRepository;
import com.testsigma.util.XLSUtil;
import com.testsigma.web.request.EnvironmentRunResultRequest;
import com.testsigma.web.request.TestDeviceResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestDeviceResultService {

  private final TestDeviceResultRepository testDeviceResultRepository;
  private final TestPlanResultService testPlanResultService;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final TestSuiteResultService testSuiteResultService;
  private final TestCaseResultService testCaseResultService;
  private final TestDeviceResultMapper testDeviceResultMapper;

  public TestDeviceResult find(Long id) throws ResourceNotFoundException {
    return testDeviceResultRepository.findById(id)
      .orElseThrow(
        () -> new ResourceNotFoundException("Could not find resource with id:" + id));
  }

  public TestDeviceResult findQueuedHybridEnvironment(Long id) {
    return testDeviceResultRepository.findFirstByTestDeviceAgentIdAndTestPlanLabTypeAndTestPlanResultResultOrderByIdAsc(id, TestPlanLabType.Hybrid, ResultConstant.QUEUED);
  }

  public Page<TestDeviceResult> findAll(Specification<TestDeviceResult> spec, Pageable pageable) {
    return this.testDeviceResultRepository.findAll(spec, pageable);
  }

  public TestDeviceResult findByTestPlanResultIdAndTestDeviceId(Long testPlanResultId, Long prerequisiteTestDevicesId) {
    return testDeviceResultRepository.findByTestPlanResultIdAndTestDeviceId(testPlanResultId, prerequisiteTestDevicesId);
  }

  public List<TestDeviceResult> findAllByTestPlanResultIdAndResultIsNot(Long testPlanResultId, ResultConstant notInResult) {
    return testDeviceResultRepository.findAllByTestPlanResultIdAndResultIsNot(testPlanResultId, notInResult);
  }

  public List<TestDeviceResult> findAllByTestPlanResultIdAndStatusIsNot(Long testPlanResultId, StatusConstant notInStatus) {
    return testDeviceResultRepository.findAllByTestPlanResultIdAndStatusIsNot(testPlanResultId, notInStatus);
  }

  public List<TestDeviceResult> findAllByTestPlanResultId(Long testPlanResultId) {
    return this.testDeviceResultRepository.findAllByTestPlanResultId(testPlanResultId);
  }

  public StatusConstant maxStatusByExecutionRunId(Long testPlanResultId) {
    return this.testDeviceResultRepository.maxStatusByTestPlanResultId(testPlanResultId);
  }

  public Integer countByTestPlanResultIdAndStatusIsNot(Long testPlanResultId, StatusConstant status) {
    return this.testDeviceResultRepository.countByTestPlanResultIdAndStatusIsNot(testPlanResultId, status);
  }

  public ResultConstant maxResultByTestPlanResultId(Long testPlanResultId) {
    return this.testDeviceResultRepository.maxResultByTestPlanResultId(testPlanResultId);
  }

  public List<TestDeviceResult> findAllByTestPlanResultIdAndIsVisuallyPassedIsNull(Long testPlanResultId) {
    return this.testDeviceResultRepository.findAllByTestPlanResultIdAndIsVisuallyPassedIsNull(testPlanResultId);
  }

  private List<TestDeviceResult> findAllByTestPlanResultIdAndIsVisuallyPassed(Long testPlanResultId, boolean visualResult) {
    return this.testDeviceResultRepository.findAllByTestPlanResultIdAndIsVisuallyPassed(testPlanResultId, visualResult);
  }

  public TestDeviceResult create(TestDeviceResult testDeviceResult) {
    return testDeviceResultRepository.save(testDeviceResult);
  }

  public TestDeviceResult update(TestDeviceResult testDeviceResult) {
    return testDeviceResultRepository.save(testDeviceResult);
  }

  public void updateVisualResult(TestDeviceResult testDeviceResult, boolean visualResult) {
    this.testDeviceResultRepository.updateVisualResult(testDeviceResult.getId(), visualResult);
  }

  public void updateResultOnError(String message, ResultConstant result, Long environmentResultId) {
    testSuiteResultService.stopTestSuiteResultsByEnvironmentResult(message, result, environmentResultId);
    testCaseResultService.stopTestCaseResultsByEnvironmentResult(message, result, environmentResultId);
  }

  public void markEnvironmentResultAsStopped(TestDeviceResult testDeviceResult, String message) {
    log.info(String.format("Updating environment result with result - %s, status - %s, message - %s where environment " +
      "result id is - %s ", ResultConstant.STOPPED, StatusConstant.STATUS_COMPLETED, message, testDeviceResult.getId()));

    Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
    testDeviceResult.setResult(ResultConstant.STOPPED);
    testDeviceResult.setStatus(StatusConstant.STATUS_COMPLETED);
    testDeviceResult.setMessage(message);
    testDeviceResult.setEndTime(currentTime);
    testDeviceResult.setDuration(0L);
    this.update(testDeviceResult);
    this.testSuiteResultService.stopIncompleteTestSuiteResults(ResultConstant.STOPPED, StatusConstant.STATUS_COMPLETED,
      message, 0L, currentTime, currentTime, testDeviceResult.getId(), StatusConstant.STATUS_COMPLETED
    );
    this.testCaseResultService.stopIncompleteTestCaseResults(ResultConstant.STOPPED,
      StatusConstant.STATUS_COMPLETED, message, 0L, currentTime, currentTime, testDeviceResult.getId(),
      StatusConstant.STATUS_COMPLETED);
    List<TestSuiteResult> suiteResultList = this.testSuiteResultService.findAllByEnvironmentResultId(testDeviceResult.getId());
    suiteResultList.forEach(result -> {
      this.testSuiteResultService.updateResultCounts(result.getId());
    });
  }

  public void markEnvironmentResultAsInPreFlight(TestDeviceResult testDeviceResult, StatusConstant inStatus) throws ResourceNotFoundException {
    log.info(String.format("Updating environment result with status - %s, message - %s where environment result id " +
      "is - %s ", StatusConstant.STATUS_PRE_FLIGHT, AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT, testDeviceResult.getId()));
    testDeviceResult.setExecutionInitiatedOn(new Timestamp(java.lang.System.currentTimeMillis()));
    testDeviceResult.setStatus(StatusConstant.STATUS_PRE_FLIGHT);
    testDeviceResult.setMessage(AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT);
    this.update(testDeviceResult);
    Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
    this.testSuiteResultService.updateResult(ResultConstant.QUEUED, StatusConstant.STATUS_PRE_FLIGHT,
      AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT, 0L,
      currentTime, currentTime, testDeviceResult.getId(), inStatus
    );
    this.testCaseResultService.updateResultByEnvironmentId(ResultConstant.QUEUED, StatusConstant.STATUS_PRE_FLIGHT,
      AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT, 0L,
      currentTime, currentTime, testDeviceResult.getId(), inStatus
    );
  }

  public void markEnvironmentResultAsInProgress(TestDeviceResult testDeviceResult, StatusConstant inStatus,
                                                Boolean cascade) {
    log.info("Moving EnvironmentResult[" + testDeviceResult.getId() + "] from status " + testDeviceResult.getStatus()
      + " to STATUS_IN_PROGRESS");
    if (testDeviceResult.getStatus() != StatusConstant.STATUS_IN_PROGRESS) {
      log.info(String.format("Updating environment result with status - %s, message - %s where environment result id " +
        "is - %s ", StatusConstant.STATUS_IN_PROGRESS, AutomatorMessages.MSG_EXECUTION_IN_PROGRESS, testDeviceResult.getId()));
      testDeviceResult.setExecutionInitiatedOn(new Timestamp(java.lang.System.currentTimeMillis()));
      testDeviceResult.setStatus(StatusConstant.STATUS_IN_PROGRESS);
      testDeviceResult.setMessage(AutomatorMessages.MSG_EXECUTION_IN_PROGRESS);
      this.update(testDeviceResult);
    }
    if (cascade) {
      Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
      this.testSuiteResultService.updateResult(ResultConstant.QUEUED, StatusConstant.STATUS_IN_PROGRESS,
        AutomatorMessages.MSG_EXECUTION_IN_PROGRESS, 0L,
        currentTime, currentTime, testDeviceResult.getId(), inStatus
      );
      this.testCaseResultService.updateResultByEnvironmentId(ResultConstant.QUEUED, StatusConstant.STATUS_IN_PROGRESS,
        AutomatorMessages.MSG_EXECUTION_IN_PROGRESS, 0L,
        currentTime, currentTime, testDeviceResult.getId(), inStatus
      );
    }
  }

  public void markEnvironmentResultAsQueued(TestDeviceResult testDeviceResult, StatusConstant inStatus,
                                                Boolean cascade) {
    log.info("Moving EnvironmentResult[" + testDeviceResult.getId() + "] from status " + testDeviceResult.getStatus()
            + " to STATUS_QUEUED");
    if (testDeviceResult.getStatus() != StatusConstant.STATUS_QUEUED) {
      log.info(String.format("Updating environment result with status - %s, message - %s where environment result id " +
              "is - %s ", StatusConstant.STATUS_QUEUED, AutomatorMessages.MSG_EXECUTION_QUEUED, testDeviceResult.getId()));
      testDeviceResult.setStatus(StatusConstant.STATUS_QUEUED);
      testDeviceResult.setMessage(AutomatorMessages.MSG_EXECUTION_QUEUED);
      this.update(testDeviceResult);
    }
    if (cascade) {
      Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
      this.testSuiteResultService.updateResult(ResultConstant.QUEUED, StatusConstant.STATUS_QUEUED,
              AutomatorMessages.MSG_EXECUTION_QUEUED, 0L,
              currentTime, currentTime, testDeviceResult.getId(), inStatus
      );
      this.testCaseResultService.updateResultByEnvironmentId(ResultConstant.QUEUED, StatusConstant.STATUS_QUEUED,
              AutomatorMessages.MSG_EXECUTION_QUEUED, 0L,
              currentTime, currentTime, testDeviceResult.getId(), inStatus
      );
    }
  }

  public void markEnvironmentResultAsFailed(TestDeviceResult testDeviceResult, String message, StatusConstant inStatus) {
    log.info(String.format("Updating environment result with result - %s, status - %s, message - %s where environment " +
      "result id is - %s ", ResultConstant.FAILURE, StatusConstant.STATUS_COMPLETED, message, testDeviceResult.getId()));

    Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
    testDeviceResult.setResult(ResultConstant.FAILURE);
    testDeviceResult.setStatus(StatusConstant.STATUS_COMPLETED);
    testDeviceResult.setMessage(message);
    testDeviceResult.setEndTime(currentTime);
    testDeviceResult.setDuration(currentTime.getTime() - testDeviceResult.getStartTime().getTime());
    this.update(testDeviceResult);
    List<TestSuiteResult> testSuiteResults = this.testSuiteResultService.findPendingTestSuiteResults(
      testDeviceResult, inStatus);
    testDeviceResult.setSuiteResults(testSuiteResults);
    for (TestSuiteResult testSuiteResult : testDeviceResult.getSuiteResults()) {
      log.info(String.format("Updating test suite result with result - %s, status - %s, message - %s where test suite " +
        "result id is - %s ", ResultConstant.FAILURE, StatusConstant.STATUS_COMPLETED, message, testSuiteResult.getId()));
      testSuiteResult.setResult(ResultConstant.FAILURE);
      testSuiteResult.setStatus(StatusConstant.STATUS_COMPLETED);
      testSuiteResult.setMessage(message);
      testSuiteResult.setStartTime(currentTime);
      testSuiteResult.setEndTime(currentTime);
      testSuiteResult.setDuration(0L);
      testSuiteResultService.update(testSuiteResult);
      this.testCaseResultService.updateResultByTestSuiteId(ResultConstant.FAILURE, StatusConstant.STATUS_COMPLETED,
        message, 0L, currentTime, currentTime, testSuiteResult.getId(), inStatus
      );
    }
  }

  public void updateEnvironmentConsolidateResult(Long environmentResultId) {
    testDeviceResultRepository.updateEnvironmentConsolidateResult(environmentResultId);
  }

  public void updateEnvironmentConsolidatedResults(TestDeviceResult testDeviceResult) throws TestsigmaException {
    try {
      Integer pendingTestSuiteResultCount = testSuiteResultService
        .countAllByEnvironmentResultIdAndStatusIsNot(testDeviceResult.getId(), StatusConstant.STATUS_COMPLETED);

      if (pendingTestSuiteResultCount == 0) {
        ResultConstant maxResult = testSuiteResultService.findMaxResultByEnvironmentResultId(testDeviceResult.getId());
        log.info("All test suite results in environment result[" + testDeviceResult.getId()
          + "] are done. Updating the environment result with final result - " + maxResult);
        String message = ResultConstant.SUCCESS.equals(maxResult) ? AutomatorMessages.MSG_ENVIRONMENT_COMPLETED :
          (ResultConstant.STOPPED.equals(maxResult)) ?
            AutomatorMessages.MSG_TEST_PLAN_STOPPED : AutomatorMessages.MSG_ENVIRONMENT_FAILURE;
        testDeviceResult.setResult(maxResult);
        testDeviceResult.setStatus(StatusConstant.STATUS_COMPLETED);
        testDeviceResult.setMessage(message);
        testDeviceResult.setEndTime(new Timestamp(java.lang.System.currentTimeMillis()));
        testDeviceResult.setDuration(testDeviceResult.getEndTime().getTime() - testDeviceResult.getStartTime().getTime());
        testDeviceResultRepository.save(testDeviceResult);
        this.updateResultCounts(testDeviceResult.getId());
      } else {
        log.info("Some test suite results in environment result[" + testDeviceResult.getTestPlanResultId()
          + "] are still pending. Waiting for them to finish before updating the final result");
        testDeviceResult.setResult(ResultConstant.QUEUED);
        testDeviceResult.setStatus(StatusConstant.STATUS_IN_PROGRESS);
        testDeviceResult.setMessage(AutomatorMessages.MSG_EXECUTION_IN_PROGRESS);
        testDeviceResult.setEndTime(null);
        testDeviceResult.setDuration(0L);
        testDeviceResultRepository.save(testDeviceResult);
      }
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void updateExecutionConsolidatedResults(Long testPlanResultId, Boolean updateMaxStatus)
    throws TestsigmaException {
    try {
      TestPlanResult testPlanResult = testPlanResultService.find(testPlanResultId);
      Integer incompleteEnvironments = this.countByTestPlanResultIdAndStatusIsNot(testPlanResultId,
        StatusConstant.STATUS_COMPLETED);

      if (incompleteEnvironments == 0) {
        ResultConstant maxResult = this.maxResultByTestPlanResultId(testPlanResultId);
        log.info("All environment results in execution result[" + testPlanResultId
          + "] are done. Updating the test plan result with final result. Max Result - " + maxResult);

        testPlanResultService.updateExecutionResult(maxResult, testPlanResult);
        testPlanResultService.updateResultCounts(testPlanResult);
      } else {
        log.info("Some environment results in execution result[" + testPlanResultId
          + "] are still pending. Waiting for them to finish before updating the final result");
        if (updateMaxStatus) {
          StatusConstant maxStatus = this.maxStatusByExecutionRunId(testPlanResultId);

          if ((maxStatus == StatusConstant.STATUS_COMPLETED) || (maxStatus == StatusConstant.STATUS_PRE_FLIGHT) || (maxStatus == StatusConstant.STATUS_QUEUED)) {
            maxStatus = StatusConstant.STATUS_IN_PROGRESS;
          }
          String message = AutomatorMessages.MSG_EXECUTION_IN_PROGRESS;
          log.info("Received update request for max status for execution - " + testPlanResultId
            + "]. Updating the test plan result with max status. Max Status - " + maxStatus);
          testPlanResultService.markTestPlanResultstatus(testPlanResult, maxStatus, message);
        }
      }
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void updateResultCounts(Long environmentResultId) {
    this.testDeviceResultRepository.updateTotalTestCaseResultsCount(environmentResultId);
    this.testDeviceResultRepository.updatePassedTestCaseResultsCount(environmentResultId);
    this.testDeviceResultRepository.updateFailedTestCaseResultsCount(environmentResultId);
    this.testDeviceResultRepository.updateAbortedTestCaseResultsCount(environmentResultId);
    this.testDeviceResultRepository.updateNotExecutedTestCaseResultsCount(environmentResultId);
    this.testDeviceResultRepository.updateQueuedTestCaseResultsCount(environmentResultId);
    this.testDeviceResultRepository.updateStoppedTestCaseResultsCount(environmentResultId);
  }

  public void sendPendingTestPlans() {
    List<Long> environmentResultIds = testDeviceResultRepository.findAllPendingEnvironments();
    List<TestDeviceResult> testDeviceResults = testDeviceResultRepository.findAllById(environmentResultIds);

    if (testDeviceResults.size() > 0) {
      log.info("Found " + testDeviceResults.size() + " pending environments, proceeding with execution...");
      if (!testDeviceResults.isEmpty()) {
        try {
          Map<Long, List<TestDeviceResult>> envResultMap = new HashMap<>();
          Map<Long, AbstractTestPlan> executionMap = new HashMap<>();
          Map<Long, TestPlanResult> testPlanResultMap = new HashMap<>();

          for (TestDeviceResult pendingTestDeviceResult : testDeviceResults) {
            TestPlanResult pendingTestPlanResult = testPlanResultService.find(
              pendingTestDeviceResult.getTestPlanResultId());
            AbstractTestPlan execution;
            if (pendingTestPlanResult.getTestPlan() != null)
              execution = pendingTestPlanResult.getTestPlan();
            else
              execution = pendingTestPlanResult.getDryTestPlan();

            List<TestDeviceResult> pendingTestDeviceResults = envResultMap.getOrDefault(
              execution.getId(), new ArrayList<>());
            pendingTestDeviceResults.add(pendingTestDeviceResult);
            envResultMap.put(execution.getId(), pendingTestDeviceResults);
            executionMap.put(execution.getId(), execution);
            testPlanResultMap.put(execution.getId(), pendingTestPlanResult);
          }

          for (Long key : envResultMap.keySet()) {
            AbstractTestPlan testPlan = executionMap.get(key);
            TestPlanResult testPlanResult = testPlanResultMap.get(key);
            List<TestDeviceResult> envResults = envResultMap.get(key);
            try {
              AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
              agentExecutionService.setTestPlan(testPlan);
              agentExecutionService.setTestPlanResult(testPlanResult);
              for(TestDeviceResult testDeviceResult : envResults ) {
                agentExecutionService.processResultEntries(testDeviceResult, StatusConstant.STATUS_QUEUED);
              }
            } catch (Exception e) {
              log.error(e.getMessage(), e);
              String message = " Error while sending pending test plans for test plan result - " + testPlanResult.getId();
              log.error(message);
              throw e;
            }
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
          String environmentIds = testDeviceResults.stream().map(er -> er.getId().toString())
            .collect(Collectors.joining(","));
          String message = "Error while processing environment results [" + environmentIds + "]";
          log.error(message);
        }
      }
    } else {
      log.info("There are no pending environments to run");
    }
  }

  public List<EnvironmentEntityDTO> getHybridEnvironmentEntitiesInPreFlight(List<TestDeviceResult> testDeviceResults) {
    List<EnvironmentEntityDTO> environmentEntityDTOS = new ArrayList<>();
    Map<Long, List<TestDeviceResult>> envResultMap = new HashMap<>();
    Map<Long, AbstractTestPlan> executionMap = new HashMap<>();
    Map<Long, TestPlanResult> testPlanResultMap = new HashMap<>();
    try {
      for (TestDeviceResult pendingTestDeviceResult : testDeviceResults) {
        TestPlanResult pendingTestPlanResult = testPlanResultService.find(pendingTestDeviceResult.getTestPlanResultId());
        AbstractTestPlan execution;
        if (pendingTestPlanResult.getTestPlan() != null)
          execution = pendingTestPlanResult.getTestPlan();
        else
          execution = pendingTestPlanResult.getDryTestPlan();

        List<TestDeviceResult> pendingTestDeviceResults = envResultMap.getOrDefault(execution.getId(), new ArrayList<>());
        pendingTestDeviceResults.add(pendingTestDeviceResult);
        envResultMap.put(execution.getId(), pendingTestDeviceResults);
        executionMap.put(execution.getId(), execution);
        testPlanResultMap.put(execution.getId(), pendingTestPlanResult);
      }

      for (Long key : envResultMap.keySet()) {
        AbstractTestPlan exe = executionMap.get(key);
        TestPlanResult exeResult = testPlanResultMap.get(key);
        List<TestDeviceResult> envResults = envResultMap.get(key);
        try {
          AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
          agentExecutionService.setTestPlan(exe);
          agentExecutionService.setTestPlanResult(exeResult);
          for (TestDeviceResult testDeviceResult : envResults) {
            Boolean cascade = Boolean.TRUE;
            EnvironmentEntityDTO environmentEntityDTO = agentExecutionService.loadEnvironment(testDeviceResult,
                    StatusConstant.STATUS_PRE_FLIGHT);
            environmentEntityDTOS.add(environmentEntityDTO);
            this.markEnvironmentResultAsInProgress(testDeviceResult, StatusConstant.STATUS_PRE_FLIGHT, cascade);
          }
          this.updateExecutionConsolidatedResults(exeResult.getId(), Boolean.TRUE);
        } catch (Exception e) {
          log.error(" Error while sending pending test plans for test plan result - " + exeResult.getId());
          throw e;
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      String environmentIds = testDeviceResults.stream().map(er -> er.getId().toString())
        .collect(Collectors.joining(","));
      String message = "Error while processing environment results [" + environmentIds + "]";
      log.error(message);
    }
    return environmentEntityDTOS;
  }

  public void propagateVisualResult(TestDeviceResult testDeviceResult) {
    List<TestDeviceResult> failedList = findAllByTestPlanResultIdAndIsVisuallyPassed(testDeviceResult.getTestPlanResultId(), false);
    TestPlanResult testPlanResult = testDeviceResult.getTestPlanResult();
    testPlanResultService.updateVisualResult(testPlanResult, failedList.isEmpty());
  }

  public void updateResult(EnvironmentRunResultRequest environmentResultRequest) throws Exception {
    TestDeviceResult testDeviceResult = find(environmentResultRequest.getId());
    Timestamp firstTestCase = testCaseResultService.findMinTimeStampByEnvironmentResultId(testDeviceResult.getId());
    testDeviceResult.setSessionCreatedOn(ObjectUtils.defaultIfNull(firstTestCase, new Timestamp(System.currentTimeMillis())));
    testDeviceResultMapper.merge(environmentResultRequest, testDeviceResult);
    testDeviceResult = update(testDeviceResult);
    if (environmentResultRequest.getErrorCode() != null) {
      updateResultOnError(environmentResultRequest.getMessage(),environmentResultRequest.getResult(), environmentResultRequest.getId());
    }
    updateEnvironmentConsolidatedResults(testDeviceResult);
    updateExecutionConsolidatedResults(testDeviceResult.getTestPlanResultId(),
      Boolean.FALSE);
    sendPendingTestPlans();
    updateResultCounts(testDeviceResult.getId());
  }

  public void updateResultData(TestDeviceResultRequest testDeviceResultRequest) throws ResourceNotFoundException {
    TestDeviceResult testDeviceResult = find(testDeviceResultRequest.getId());
    testDeviceResultMapper.mergeRequest(testDeviceResultRequest, testDeviceResult);
    update(testDeviceResult);

  }

  public TestDeviceResult getLastReRunResult(TestDeviceResult parentRunResult){
    if(parentRunResult.getChildResult() == null)
      return parentRunResult;
    return getLastReRunResult(parentRunResult.getChildResult());

  }

  public TestDeviceResult getFirstParentResult(Long childResultId) throws ResourceNotFoundException {
    TestDeviceResult childResult = find(childResultId);
    if(childResult.getReRunParentId() == null)
      return childResult;
    return getFirstParentResult(childResult.getReRunParentId());
  }

  public void export(TestDeviceResult testDeviceResult, XLSUtil wrapper) throws ResourceNotFoundException {
    wrapper.getWorkbook().setSheetName(wrapper.getWorkbook().getSheetIndex(wrapper.getSheet()),
            "Run result summary");
    setResultDetails(testDeviceResult, wrapper);
    setTestCasesSummary(testDeviceResult, wrapper);
    setDetailedTestCaseList(testDeviceResult, wrapper);
  }

  private void setTestCasesSummary(TestDeviceResult environmentResult, XLSUtil wrapper) {
    setHeading(wrapper, "Summary");
    Object[] keys = {"Total Test Cases", "Queued", "Passed", "Failed", "Aborted", "Not Executed", "Stopped"};
    Object[] counts = {environmentResult.getTotalCount(), environmentResult.getQueuedCount(),
            environmentResult.getPassedCount(), environmentResult.getFailedCount(), environmentResult.getAbortedCount(),
            environmentResult.getNotExecutedCount(),
            //environmentResult.getPreRequisiteFailedCount(),
            environmentResult.getStoppedCount()};
    setCellsHorizontally(wrapper, keys, true);
    setCellsHorizontally(wrapper, counts, false);
  }

  private void setResultDetails(TestDeviceResult testDeviceResult, XLSUtil wrapper)
          throws ResourceNotFoundException {
    setHeading(wrapper, "Execution Details");
    setDetailsKeyValue("Test Machine Name", testDeviceResult.getTestDevice().getTitle(), wrapper);
    setDetailsKeyValue("Test Plan Name", testDeviceResult.getTestDevice().getTestPlan().getName(),
            wrapper);
    if (testDeviceResult.getTestDevice().getTestPlan().getDescription() != null)
      setDetailsKeyValue("Description", testDeviceResult.getTestDevice().getTestPlan().getDescription()
              .replaceAll("\\&([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-fA-F]{1,6});|\\<.*?\\>", ""), wrapper);
    setDetailsKeyValue("RunId", testDeviceResult.getTestPlanResult().getId().toString(), wrapper);
    setDetailsKeyValue("Build No", testDeviceResult.getTestPlanResult().getBuildNo(), wrapper);
//    setDetailsKeyValue("Triggered By", userService.find(testDeviceResult.getExecutionResult().getExecutedBy()).getUserName(), wrapper);
    setDetailsKeyValue("Execution Start Time", testDeviceResult.getTestPlanResult().getStartTime().toString(), wrapper);
    setDetailsKeyValue("Execution End Time", testDeviceResult.getEndTime() != null ? testDeviceResult.getEndTime().toString() : "-", wrapper);
    setDetailsKeyValue("Execution Result",
            testDeviceResult.getTestPlanResult().getResult().getName(),
            wrapper);
    setDetailsKeyValue("Execution Message", testDeviceResult.getTestPlanResult().getMessage(), wrapper);
  }

  private void setDetailsKeyValue(String key, String value, XLSUtil wrapper) {
    Integer count = 0;
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    row.createCell(count).setCellValue(key);
    row.getCell(count).setCellStyle(XLSUtil.getSecondAlignStyle(wrapper));
    row.createCell(++count).setCellValue(value);
  }

  private void setDetailedTestCaseList(TestDeviceResult testDeviceResult, XLSUtil wrapper) {
    setHeading(wrapper, "Test Cases List");
    String[] keys = {"Test Case", "Test Suite", "Result", "Start Time", "End Time", "Visual Test Results"};
    setCellsHorizontally(wrapper, keys, true);
    List<TestCaseResult> testCaseResults = testCaseResultService.findAllByEnvironmentResultId(testDeviceResult.getId());
    for (TestCaseResult testCaseResult : testCaseResults) {
      Object[] values = {testCaseResult.getTestCase().getName(), testCaseResult.getTestSuite().getName(),
              testCaseResult.getResult().getName(), testCaseResult.getStartTime(),
              testCaseResult.getEndTime(), testCaseResult.getIsVisuallyPassed() == null ? "N/A" : testCaseResult.getIsVisuallyPassed() ? "PASS" :"FAIL"};
      setCellsHorizontally(wrapper, values, false);
    }
  }

  private void setCellsHorizontally(XLSUtil wrapper, Object[] keys, boolean isBold) {
    Integer count = -1;
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    for (Object key : keys) {
      row.createCell(++count).setCellValue(key.toString());
    }
    if (isBold) {
      count = -1;
      for (Object key : keys) {
        row.getCell(++count).setCellStyle(XLSUtil.getSecondAlignStyle(wrapper));
      }
    }
  }

  private void setHeading(XLSUtil wrapper, String key) {
    wrapper.getDataRow(wrapper, wrapper.getNewRow());
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    CellStyle header = XLSUtil.getTableHeaderStyle(wrapper);
    row.setRowStyle(header);
    row.createCell(1).setCellValue(key);
    row.getCell(1).setCellStyle(header);
  }
}
