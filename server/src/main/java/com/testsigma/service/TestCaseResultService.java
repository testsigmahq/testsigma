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
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.mapper.TestCaseResultMapper;
import com.testsigma.mapper.TestDataProfileMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestCaseResultRepository;
import com.testsigma.tasks.VisualTestingTask;
import com.testsigma.web.request.TestCaseResultRequest;
import com.testsigma.web.request.TestStepResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestCaseResultService {
  private final TestCaseResultRepository testCaseResultRepository;
  private final TestCaseResultMapper testCaseResultMapper;
  private final TestStepResultService testStepResultService;
  private final TestDataProfileService testDataProfileService;
  private final TestDataProfileMapper testDataProfileMapper;
  private final TestSuiteResultService testSuiteResultService;
  private final TestDeviceResultService testDeviceResultService;
  private final TestPlanResultService testPlanResultService;
  private final VisualTestingService visualTestingService;
  private final StorageConfigService storageConfigService;

  public Page<TestCaseResult> findAll(Specification<TestCaseResult> spec, Pageable pageable) {
    return this.testCaseResultRepository.findAll(spec, pageable);
  }

  public List<TestCaseResult> findAllByTestPlanResultId(Long testPlanResultId) {
    return this.testCaseResultRepository.findAllByTestPlanResultId(testPlanResultId);
  }

  public Timestamp findMinTimeStampByEnvironmentResultId(Long environmentResultId) {
    return this.testCaseResultRepository.findMinTimeStampByEnvironmentResultId(environmentResultId);
  }

  public List<TestCaseResult> findActiveSuiteTestCaseResults(Long suiteResultId, StatusConstant status) {
    return this.testCaseResultRepository.findByActiveSuiteTestCaseResults(suiteResultId, status);
  }

  public List<TestCaseResult> findAllByParentIdAndStatus(Long parentId, StatusConstant status) {
    return this.testCaseResultRepository.findAllByParentIdAndStatus(parentId, status);
  }

  public List<TestCaseResult> findAllBySuiteResultIdAndResultIsNot(Long testSuiteResultId, ResultConstant result) {
    return this.testCaseResultRepository.findAllBySuiteResultIdAndResultIsNot(testSuiteResultId, result);
  }

  public List<TestCaseResult> findAllBySuiteResultId(Long suiteResultId) {
    return this.testCaseResultRepository.findAllBySuiteResultId(suiteResultId);
  }

  public List<TestCaseResult> findAllBySuiteResultIdAndTestCaseId(Long suiteResultId, Long preRequisite) {
    return this.testCaseResultRepository.findAllBySuiteResultIdAndTestCaseId(suiteResultId, preRequisite);
  }

  public List<TestCaseResult> findByTestCaseResultIds(List<Long> testCaseResultIds) {
    return this.testCaseResultRepository.findAllById(testCaseResultIds);
  }

  public List<TestCaseResult> findAllByEnvironmentResultId(Long environmentResultId) {
    return testCaseResultRepository.findAllByEnvironmentResultId(environmentResultId);
  }

  private List<TestCaseResult> findAllBySuiteResultIdAndIsVisuallyPassed(Long suiteResultId) {
    return this.testCaseResultRepository.findAllBySuiteResultIdAndIsVisuallyPassed(suiteResultId, false);
  }

  public TestCaseResult find(Long testCaseResultId) throws ResourceNotFoundException {
    return testCaseResultRepository.findById(testCaseResultId)
      .orElseThrow(() -> new ResourceNotFoundException(
        "TestCaseResult Resource not found with id:" + testCaseResultId));
  }

  public Integer countAllBySuiteResultIdAndStatusIsNot(Long testSuiteResultId, StatusConstant status) {
    return testCaseResultRepository.countAllBySuiteResultIdAndStatusIsNot(testSuiteResultId, status);
  }

  public ResultConstant findBySuiteResultIdAndMaxResult(Long testSuiteResultId) {
    return testCaseResultRepository.findMaximumResultBySuiteId(testSuiteResultId);
  }

  public TestCaseResult create(TestCaseResult testCaseResult) {
    return this.testCaseResultRepository.save(testCaseResult);
  }

  public TestCaseResult update(TestCaseResult testCaseResult) {
    return testCaseResultRepository.save(testCaseResult);
  }

  public void updateResultByEnvironmentId(ResultConstant result, StatusConstant status, String message, Long duration,
                                          Timestamp startTime, Timestamp endTime, Long environmentRunId,
                                          StatusConstant statusConstant) {
    log.info(String.format("Updating test cases with result - %s, status - %s, message - %s" +
      "with environment result id - %s and status in %s", result, status, message, environmentRunId, statusConstant));

    testCaseResultRepository.updateTestCaseResultByEnvironmentId(result, status, message, duration, startTime, endTime,
      environmentRunId, statusConstant);
  }

  public void updateTestCaseResultByEnvironmentIdAndResult(ResultConstant result, StatusConstant status, String message, Long duration,
                                                           Timestamp startTime, Timestamp endTime, Long environmentRunId,
                                                           ResultConstant resultConstant) {
    log.info(String.format("Updating test cases with result - %s, status - %s, message - %s" +
            "with environment result id - %s and result in %s", result, status, message, environmentRunId, resultConstant));

    testCaseResultRepository.updateTestCaseResultByEnvironmentIdAndResult(result, status, message, duration, startTime, endTime,
            environmentRunId, resultConstant);
  }

  public void updateResultByTestSuiteId(ResultConstant result, StatusConstant status, String message, Long duration,
                                        Timestamp startTime, Timestamp endTime, Long testSuiteResultId,
                                        StatusConstant statusConstant) {
    log.info(String.format("Updating test cases with result - %s, status - %s, message - %s" +
      "with test suite result id - %s and status in %s", result, status, message, testSuiteResultId, statusConstant));

    testCaseResultRepository.updateTestCaseResultBySuiteResultId(result, status, message, duration, startTime, endTime,
      testSuiteResultId, statusConstant);
  }

  public void updateResult(TestCaseResultRequest testCaseResultRequest) throws ResourceNotFoundException,
    TestsigmaDatabaseException, UnsupportedEncodingException {
    TestCaseResult testCaseResult = find(testCaseResultRequest.getId());
    if (testCaseResultRequest.getResult() == null || testCaseResultRequest.getResult().equals(ResultConstant.QUEUED)) {
      this.updateTestCaseSteps(testCaseResultRequest);
      this.updateResultCounts(testCaseResult);
    } else {
      this.updateTestCaseSteps(testCaseResultRequest);
      testCaseResultMapper.merge(testCaseResultRequest, testCaseResult);
      testCaseResult.setStatus(StatusConstant.STATUS_COMPLETED);
      update(testCaseResult);
      if (!storageConfigService.getStorageConfig().getStorageType().equals(StorageType.ON_PREMISE))
        initiateScreenshotAnalysis(testCaseResult);
      if (!testCaseResult.getIsDataDriven())
        updateResultCounts(testCaseResult);
      if (testCaseResult.getParentId() != null) {
        updateIterationResultCount(testCaseResult.getParentResult());
      }
      testSuiteResultService.updateResultCounts(testCaseResult.getSuiteResultId());
      testDeviceResultService.updateResultCounts(testCaseResult.getEnvironmentResultId());
    }
  }

  public void updateTestCaseSteps(TestCaseResultRequest testCaseResultRequest) throws TestsigmaDatabaseException,
    UnsupportedEncodingException,
    ResourceNotFoundException {
    TestDataSet testDataSet = null;
    TestData testData = null;
    Map<String, TestDataSet> testDataSetList;

    if (testCaseResultRequest.getTestDataId() != null) {
      testData = testDataProfileService.find(testCaseResultRequest.getTestDataId());
      testDataSetList = testDataProfileMapper.map(testData);

      if (!testDataSetList.isEmpty()) {
        testDataSet = testDataSetList.get(testCaseResultRequest.getTestDataSetName());
      }
    }

    List<TestStepResultRequest> testCaseStepResultList = testCaseResultRequest.getTestCaseStepResults();
    if (!testCaseStepResultList.isEmpty()) {
      if (testCaseResultRequest.getCurrentIndex() == 0) {
        Integer removedSteps = testStepResultService.deleteByTestCaseResultIdAndEnvironmentResultId(
          testCaseResultRequest.getId(), testCaseResultRequest.getEnvRunId());
      }
      testStepResultService.createTestCaseSteps(testCaseResultRequest, testData, testDataSet);
    } else {
      log.info("There are no test step results in this test case result[" + testCaseResultRequest.getId() + "]...");
    }
  }

  public void updateParentResult(TestCaseResult result) throws Exception {
    TestCaseResult parentTestCaseResult = find(result.getParentId());
    if (result.getResult() == ResultConstant.QUEUED) {
      parentTestCaseResult.setResult(result.getResult());
      parentTestCaseResult.setStatus(StatusConstant.STATUS_IN_PROGRESS);
      parentTestCaseResult.setMessage(result.getMessage());
      parentTestCaseResult.setEndTime(new Timestamp(System.currentTimeMillis()));
      parentTestCaseResult.setDuration(0L);
    } else {
      Integer pendingTestCaseResultCount = testCaseResultRepository.countAllByParentIdAndStatusIsNot(
        parentTestCaseResult.getId(), StatusConstant.STATUS_COMPLETED);

      if (pendingTestCaseResultCount == 0) {
        ResultConstant maxResult =
          testCaseResultRepository.findMaximumResultByParentId(parentTestCaseResult.getId());
        Timestamp endTime = testCaseResultRepository.findMaximumEndTimeByParentId(parentTestCaseResult.getId());
        Timestamp startTime = testCaseResultRepository.findMinimumStartTimeByParentId(parentTestCaseResult.getId());
        startTime = ObjectUtils.defaultIfNull(startTime, result.getStartTime());
        endTime = ObjectUtils.defaultIfNull(endTime, result.getEndTime());
        parentTestCaseResult.setResult(maxResult);
        parentTestCaseResult.setStatus(StatusConstant.STATUS_COMPLETED);
        parentTestCaseResult.setMessage(result.getMessage());
        parentTestCaseResult.setStartTime(startTime);
        parentTestCaseResult.setEndTime(endTime);
        parentTestCaseResult.setDuration(startTime.getTime() - endTime.getTime());
      }
    }
    update(parentTestCaseResult);
    updateIterationResultCount(parentTestCaseResult);
  }

  public void updateResultCounts(TestCaseResult testCaseResult) {
    log.info("Updating result counts for test case result - " + testCaseResult.getId());
    this.testCaseResultRepository.updateTotalTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updatePassedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateFailedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateAbortedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateNotExecutedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateQueuedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateStoppedTestCaseResultsCount(testCaseResult.getId());
    this.testPlanResultService.updateResultCounts(testCaseResult.getTestPlanResult());
  }

  public void updateIterationResultCount(TestCaseResult testCaseResult) {
    log.info("Updating iteration result counts for test case result - " + testCaseResult.getId());
    this.testCaseResultRepository.updateIterationTotalTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateIterationPassedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateIterationFailedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateIterationAbortedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateIterationNotExecutedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateIterationQueuedTestCaseResultsCount(testCaseResult.getId());
    this.testCaseResultRepository.updateIterationStoppedTestCaseResultsCount(testCaseResult.getId());
  }

  public void propagateVisualResult(TestCaseResult testCaseResult) throws ResourceNotFoundException {
    TestCaseResult result = testCaseResult;
    if (testCaseResult.getParentId() != null) {
      result = find(testCaseResult.getParentId());
      updateVisualResult(result, true);
    }
    List<TestCaseResult> failedList = findAllBySuiteResultIdAndIsVisuallyPassed(result.getSuiteResultId());
    TestSuiteResult testSuiteResult = testSuiteResultService.find(result.getSuiteResultId());
    testSuiteResultService.updateVisualResult(testSuiteResult, failedList.isEmpty());
    List<TestSuiteResult> pendingList = testSuiteResultService.findAllByEnvironmentResultIdAndIsVisuallyPassedIsNull(testSuiteResult.getEnvironmentResultId());
    if (pendingList.isEmpty()) {
      testSuiteResultService.propagateVisualResult(testSuiteResult);
    }
  }

  public void updateVisualResult(TestCaseResult testCaseResult, boolean isVisuallyPassed) {
    this.testCaseResultRepository.updateVisualResult(testCaseResult.getId(), isVisuallyPassed);
  }

  public void markTestCaseResultAsInProgress(TestCaseResult testCaseResult) throws ResourceNotFoundException {
    log.info(String.format("Updating test case result with status - %s, message - %s with id %s",
      StatusConstant.STATUS_IN_PROGRESS, AutomatorMessages.MSG_EXECUTION_IN_PROGRESS, testCaseResult.getId()));

    testCaseResult.setStatus(StatusConstant.STATUS_IN_PROGRESS);
    testCaseResult.setMessage(AutomatorMessages.MSG_EXECUTION_IN_PROGRESS);
    testCaseResult.setStartTime(new Timestamp(java.lang.System.currentTimeMillis()));
    testCaseResultRepository.save(testCaseResult);
    if (testCaseResult.getParentId() != null) {
      TestCaseResult parentTestCaseResult = this.find(testCaseResult.getParentId());
      if (parentTestCaseResult.getStatus() != StatusConstant.STATUS_IN_PROGRESS) {
        log.info(String.format("Updating test case result(parent) with status - %s, message - %s with id %s",
          StatusConstant.STATUS_IN_PROGRESS, AutomatorMessages.MSG_EXECUTION_IN_PROGRESS, parentTestCaseResult.getId()));
        parentTestCaseResult.setStatus(StatusConstant.STATUS_IN_PROGRESS);
        parentTestCaseResult.setMessage(AutomatorMessages.MSG_EXECUTION_IN_PROGRESS);
        this.update(parentTestCaseResult);
      }
    }
  }

  public void stopIncompleteTestCaseResults(ResultConstant result, StatusConstant status,
                                            String message, Long duration, Timestamp startTime,
                                            Timestamp endTime, Long environmentRunId,
                                            StatusConstant notInStatus) {
    log.info(String.format("Updating test cases with result - %s, status - %s, message - %s" +
      "with environment result id - %s and status not in %s", result, status, message, environmentRunId, notInStatus));

    testCaseResultRepository.stopIncompleteTestCaseResults(result, status, message, duration, startTime,
      endTime, environmentRunId, notInStatus);
    testStepResultService.stopIncompleteTestStepResults(result, message, duration, startTime,
      endTime, environmentRunId);
    List<TestCaseResult> testCaseResults = testCaseResultRepository.findAllByEnvironmentResultId(environmentRunId);
    testCaseResults.forEach(testCaseResult -> {
      updateResultCounts(testCaseResult);
    });
  }

  public void stopTestCaseResultsByEnvironmentResult(String message, ResultConstant result, Long environmentRunId) {
    log.info(String.format("Updating test cases with result - %s, message - %s with environment result id %s",
      result, message, environmentRunId));

    Timestamp currentTime = new Timestamp(java.lang.System.currentTimeMillis());
    testCaseResultRepository.updateResultForStopped(result, message, currentTime, currentTime,
      0L, environmentRunId);
  }

  private void initiateScreenshotAnalysis(TestCaseResult result) {
    try {
      new VisualTestingTask(result, visualTestingService).start();
    } catch (Exception e) {
      log.error("Error in screenshot comparison/analysis", e);
    }
  }

  public void updateResultData(TestCaseResultRequest testCaseResultRequest) throws ResourceNotFoundException {
    TestCaseResult testCaseResult = find(testCaseResultRequest.getId());
    testCaseResultMapper.merge(testCaseResultRequest, testCaseResult);
    update(testCaseResult);
  }

  public TestCaseResult getLastReRunResult(TestCaseResult parentRunResult){
    if(parentRunResult.getChildResult() == null)
      return parentRunResult;
    return getLastReRunResult(parentRunResult.getChildResult());

  }
}
