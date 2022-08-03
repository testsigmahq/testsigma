/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.constants.MessageConstants;
import com.testsigma.constants.AutomatorMessages;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestSuiteResultMapper;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestDeviceResult;
import com.testsigma.model.TestSuiteResult;
import com.testsigma.repository.TestSuiteResultRepository;
import com.testsigma.web.request.TestSuiteResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestSuiteResultService {
  private final TestSuiteResultRepository testSuiteResultRepository;
  private final TestCaseResultService testCaseResultService;
  private final TestDeviceResultService testDeviceResultService;
  private final TestSuiteResultMapper testSuiteResultMapper;

  public TestSuiteResult find(Long testsuiteResultId) throws ResourceNotFoundException {
    return testSuiteResultRepository.findById(testsuiteResultId)
      .orElseThrow(() -> new ResourceNotFoundException(
        "TestSuiteResult Resource not found with id:" + testsuiteResultId));
  }

  public Page<TestSuiteResult> findAll(Specification<TestSuiteResult> spec, Pageable pageable) {
    return this.testSuiteResultRepository.findAll(spec, pageable);
  }

  public List<TestSuiteResult> findAllByEnvironmentResultId(Long environmentResultId) {
    return this.testSuiteResultRepository.findAllByEnvironmentResultId(environmentResultId);
  }

  public List<TestSuiteResult> findAllByEnvironmentResultIdAndResultIsNot(Long environmentResultId, ResultConstant result) {
    return this.testSuiteResultRepository.findAllByEnvironmentResultIdAndResultIsNot(environmentResultId, result);
  }

  public TestSuiteResult findByEnvironmentResultIdAndSuiteId(Long environmentResultId, Long suiteId) {
    return this.testSuiteResultRepository.findByEnvironmentResultIdAndSuiteId(environmentResultId, suiteId);
  }

  public List<TestSuiteResult> findBySuiteResultIds(List<Long> testSuiteResultIds) {
    return this.testSuiteResultRepository.findAllById(testSuiteResultIds);
  }

  public List<TestSuiteResult> findPendingTestSuiteResults(TestDeviceResult testDeviceResult, StatusConstant status) {
    return this.testSuiteResultRepository.
      findByEnvironmentResultIdAndStatusOrderByPositionAsc(testDeviceResult.getId(), status);
  }

  private List<TestSuiteResult> findAllByEnvironmentResultIdAndIsVisuallyPassed(Long environmentResultId) {
    return this.testSuiteResultRepository.findAllByEnvironmentResultIdAndIsVisuallyPassed(environmentResultId,
      false);
  }

  public List<TestSuiteResult> findAllByEnvironmentResultIdAndIsVisuallyPassedIsNull(Long environmentResultId) {
    return this.testSuiteResultRepository.findAllByEnvironmentResultIdAndIsVisuallyPassedIsNull(environmentResultId);
  }

  public Integer countAllByEnvironmentResultIdAndStatusIsNot(Long environmentResultId, StatusConstant status) {
    return this.testSuiteResultRepository.countAllByEnvironmentResultIdAndStatusIsNot(environmentResultId, status);
  }

  public ResultConstant findMaxResultByEnvironmentResultId(Long environmentResultId) {
    return testSuiteResultRepository.findMaxResultByEnvironmentResultId(environmentResultId);
  }

  public TestSuiteResult create(TestSuiteResult testSuiteResult) {
    return this.testSuiteResultRepository.saveAndFlush(testSuiteResult);
  }

  public TestSuiteResult update(TestSuiteResult testCaseGroupResult) {
    return testSuiteResultRepository.save(testCaseGroupResult);
  }

  public void updateResult(ResultConstant result, StatusConstant status, String message, Long duration,
                           Timestamp startTime, Timestamp endTime, Long environmentRunId,
                           StatusConstant statusConstant) {
    log.info(String.format("Updating test suites with result - %s, status - %s, message - %s with environment result " +
      "id - %s and status is %s ", result, status, message, environmentRunId, statusConstant));

    testSuiteResultRepository.updateTestSuiteResult(result, message, status, duration, startTime, endTime,
      environmentRunId, statusConstant);
  }

  public void updateResultByResult(ResultConstant result, StatusConstant status, String message, Long duration,
                           Timestamp startTime, Timestamp endTime, Long environmentRunId,
                           ResultConstant resultConstant) {
    log.info(String.format("Updating test suites with result - %s, status - %s, message - %s with environment result " +
            "id - %s and result is %s ", result, status, message, environmentRunId, resultConstant));

    testSuiteResultRepository.updateTestSuiteResultByResultCheck(result, message, status, duration, startTime, endTime,
            environmentRunId, resultConstant);
  }

  public void stopTestSuiteResultsByEnvironmentResult(String message, ResultConstant result, Long environmentRunId) {
    log.info(String.format("Updating test suites with result - %s, status - %s, message - %s with environment result " +
      "id - %s ", result, StatusConstant.STATUS_COMPLETED, message, environmentRunId));

    Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
    testSuiteResultRepository.updateResultForStopped(result, message,
      currentTime, currentTime, 0L, environmentRunId);
  }

  public void stopIncompleteTestSuiteResults(ResultConstant result, StatusConstant status, String message, Long duration,
                                             Timestamp startTime, Timestamp endTime, Long environmentRunId,
                                             StatusConstant notInStatus) {
    log.info(String.format("Updating test suites with result - %s, status - %s, message - %s" +
      "with environment result id - %s and status not in %s", result, status, message, environmentRunId, notInStatus));

    testSuiteResultRepository.stopIncompleteTestSuiteResults(result, message, status, duration, startTime, endTime,
      environmentRunId, notInStatus);
  }

  public void markTestSuiteResultAsInFlight(TestSuiteResult testSuiteResult, StatusConstant status) {
    log.info(String.format("Updating test suites with status - %s, message - %s with test suite result id - %s",
      StatusConstant.STATUS_PRE_FLIGHT, AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT, testSuiteResult.getId()));

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    testSuiteResult.setStatus(StatusConstant.STATUS_PRE_FLIGHT);
    testSuiteResult.setMessage(AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT);
    this.update(testSuiteResult);
    testCaseResultService.updateResultByTestSuiteId(ResultConstant.QUEUED, StatusConstant.STATUS_PRE_FLIGHT,
      AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT, 0L, currentTime, currentTime,
      testSuiteResult.getId(), status);
  }

  public void markTestSuiteResultAsInProgress(TestSuiteResult testSuiteResult, StatusConstant status) {
    log.info(String.format("Updating test suites with status - %s, message - %s with test suite result id - %s",
      StatusConstant.STATUS_IN_PROGRESS, AutomatorMessages.MSG_EXECUTION_IN_PROGRESS, testSuiteResult.getId()));

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    testSuiteResult.setStartTime(currentTime);
    testSuiteResult.setStatus(StatusConstant.STATUS_IN_PROGRESS);
    testSuiteResult.setMessage(AutomatorMessages.MSG_EXECUTION_IN_PROGRESS);
    this.update(testSuiteResult);
    testCaseResultService.updateResultByTestSuiteId(ResultConstant.QUEUED, StatusConstant.STATUS_IN_PROGRESS,
      AutomatorMessages.MSG_EXECUTION_PRE_FLIGHT, 0L, currentTime, currentTime,
      testSuiteResult.getId(), status);
  }

  public void markTestSuiteResultAsFailed(TestSuiteResult testSuiteResult, String message, StatusConstant status) {
    log.info(String.format("Updating test suites with result - %s, status - %s, message - %s with test suite result " +
      "id - %s", ResultConstant.FAILURE, StatusConstant.STATUS_COMPLETED, message, testSuiteResult.getId()));

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    testSuiteResult.setResult(ResultConstant.FAILURE);
    testSuiteResult.setStatus(StatusConstant.STATUS_COMPLETED);
    testSuiteResult.setMessage(message);
    testSuiteResult.setStartTime(currentTime);
    testSuiteResult.setEndTime(currentTime);
    testSuiteResult.setDuration(0L);
    this.update(testSuiteResult);
    testCaseResultService.updateResultByTestSuiteId(ResultConstant.FAILURE, StatusConstant.STATUS_COMPLETED,
      message, 0L, currentTime, currentTime,
      testSuiteResult.getId(), status);
  }

  public void updateResult(TestSuiteResultRequest testSuiteResultRequest) throws TestsigmaException {
    TestSuiteResult testCaseGroupResult = find(testSuiteResultRequest.getId());
    testSuiteResultMapper.merge(testSuiteResultRequest, testCaseGroupResult);
    TestSuiteResult testSuiteResult = update(testCaseGroupResult);
    updateConsolidatedResults(testSuiteResult);
    if (testSuiteResultRequest.getSuiteInParallel()) {
      testDeviceResultService.updateEnvironmentConsolidateResult(testSuiteResult.getEnvironmentResultId());
    }
    updateResultCounts(testSuiteResult.getId());

  }

  public void updateConsolidatedResults(TestSuiteResult testSuiteResult) throws TestsigmaException {
    try {
      Integer pendingTestCaseResultCount = testCaseResultService
        .countAllBySuiteResultIdAndStatusIsNot(testSuiteResult.getId(), StatusConstant.STATUS_COMPLETED);

      if (pendingTestCaseResultCount == 0) {
        ResultConstant maxResult = testCaseResultService.findBySuiteResultIdAndMaxResult(testSuiteResult.getId());
        log.info("All test case results in test suite result[" + testSuiteResult.getId()
          + "] are done. Updating the environment result with final result - " + maxResult);
        String message = ResultConstant.SUCCESS.equals(maxResult) ? MessageConstants.TEST_PLAN_COMPLETED :
          (ResultConstant.STOPPED.equals(maxResult)) ?
            AutomatorMessages.MSG_USER_ABORTED_EXECUTION : MessageConstants.TEST_PLAN_FAILURE;

        log.info(String.format("Updating test suites with max result - %s, status - %s, message - %s with test suite " +
          "result id - %s", maxResult, StatusConstant.STATUS_COMPLETED, message, testSuiteResult.getId()));

        testSuiteResult.setResult(maxResult);
        testSuiteResult.setStatus(StatusConstant.STATUS_COMPLETED);
        testSuiteResult.setMessage(message);
        testSuiteResult.setEndTime(new Timestamp(java.lang.System.currentTimeMillis()));
        testSuiteResult.setDuration(testSuiteResult.getEndTime().getTime() - testSuiteResult.getStartTime().getTime());
        testSuiteResultRepository.save(testSuiteResult);
      } else {
        log.info("Some test case results in test suite result[" + testSuiteResult.getId()
          + "] are still pending. Waiting for them to finish before updating the final result");
      }
    } catch (Exception e) {
      throw new TestsigmaException(e.getMessage(), e);
    }
  }

  public void updateResultCounts(Long testSuiteResultId) {
    log.info("Updating result counts for test suite id - " + testSuiteResultId);
    this.testSuiteResultRepository.updateTotalTestCaseResultsCount(testSuiteResultId);
    this.testSuiteResultRepository.updatePassedTestCaseResultsCount(testSuiteResultId);
    this.testSuiteResultRepository.updateFailedTestCaseResultsCount(testSuiteResultId);
    this.testSuiteResultRepository.updateAbortedTestCaseResultsCount(testSuiteResultId);
    this.testSuiteResultRepository.updateNotExecutedTestCaseResultsCount(testSuiteResultId);
    this.testSuiteResultRepository.updateQueuedTestCaseResultsCount(testSuiteResultId);
    this.testSuiteResultRepository.updateStoppedTestCaseResultsCount(testSuiteResultId);
  }

  public void propagateVisualResult(TestSuiteResult testSuiteResult) {
    List<TestSuiteResult> failedList = findAllByEnvironmentResultIdAndIsVisuallyPassed(
      testSuiteResult.getEnvironmentResultId());
    TestDeviceResult testDeviceResult = testSuiteResult.getTestDeviceResult();
    testDeviceResultService.updateVisualResult(testDeviceResult, failedList.isEmpty());
    List<TestDeviceResult> pendingList = testDeviceResultService.findAllByTestPlanResultIdAndIsVisuallyPassedIsNull(
      testDeviceResult.getTestPlanResultId());
    if (pendingList.isEmpty()) {
      testDeviceResultService.propagateVisualResult(testDeviceResult);
    }
  }

  public void updateVisualResult(TestSuiteResult testSuiteResult, boolean visualResult) {
    this.testSuiteResultRepository.updateVisualResult(testSuiteResult.getId(), visualResult);
  }

  public void updateResultData(TestSuiteResultRequest testCaseGroupResultRequest) throws ResourceNotFoundException {
    TestSuiteResult testCaseGroupResult = find(testCaseGroupResultRequest.getId());
    testSuiteResultMapper.merge(testCaseGroupResultRequest, testCaseGroupResult);
    update(testCaseGroupResult);
  }
}
