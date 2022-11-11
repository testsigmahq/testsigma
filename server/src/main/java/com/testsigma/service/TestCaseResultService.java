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
import com.testsigma.util.XLSUtil;
import com.testsigma.web.request.TestCaseResultRequest;
import com.testsigma.web.request.TestStepResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.*;

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
  private final TestCaseService testCaseService;
  private final StepResultScreenshotComparisonService stepResultScreenshotComparisonService;

  public Page<TestCaseResult> findAll(Specification<TestCaseResult> spec, Pageable pageable) {
    return this.testCaseResultRepository.findAll(spec, pageable);
  }

  public List<TestCaseResult> findAllByTestPlanResultId(Long id) {
    return this.testCaseResultRepository.findAllByTestPlanResultId(id);
  }

  public List<TestCaseResult> findAllByTestPlanResultIdAndResultIsNot(Long testPlanResultId, ResultConstant resultConstant) {
    return this.testCaseResultRepository.findAllByTestPlanResultIdAndResultIsNot(testPlanResultId, resultConstant);
  }

  public List<TestCaseResult> findAllBySuiteResultIdAndIsDataDrivenTrueAndResultIsNot(Long parentTestCaseId, ResultConstant result){
    return this.testCaseResultRepository.findAllBySuiteResultIdAndIsDataDrivenTrueAndResultIsNot(parentTestCaseId, result);
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

  public List<TestCaseResult> findAllBySuiteResultIdAnAndParentIdNull(Long suiteResultId) {
    return this.testCaseResultRepository.findAllBySuiteResultIdAndParentIdNull(suiteResultId);
  }

  public List<TestCaseResult> findAllBySuiteResultId(Long suiteResultId) {
    return this.testCaseResultRepository.findAllBySuiteResultId(suiteResultId);
  }

  public List<TestCaseResult> findAllBySuiteResultIdAndTestCaseIdAndResultIsNot(Long suiteResultId, Long preRequisite, ResultConstant result) {
    return this.testCaseResultRepository.findAllBySuiteResultIdAndTestCaseIdAndResultIsNot(suiteResultId, preRequisite, result);
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

  public List<TestCaseResult> findAllByParentId(Long parentId){
    return testCaseResultRepository.findAllByParentId(parentId);
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

  public List<TestCaseResult> findConsolidatedTestCaseResultsByExecutionResultId(Long executionResultId) throws ResourceNotFoundException {
    Map<String, TestCaseResult> consolidatedResults = new HashMap<>();
    findConsolidatedTestCaseResults(executionResultId, consolidatedResults);
    return new ArrayList<>(consolidatedResults.values());
  }

  private void findConsolidatedTestCaseResults(Long testPlanResultId, Map<String, TestCaseResult> consolidatedResults) throws ResourceNotFoundException {
    TestPlanResult executionResult = testPlanResultService.find(testPlanResultId);
    List<TestCaseResult> currentTestCaseResults = this.findAllByTestPlanResultId(testPlanResultId);
    currentTestCaseResults.stream().filter(testcaseResult -> {
              String key = testcaseResult.getTestDeviceResult().getTestDeviceId()
                      + "_" + testcaseResult.getTestSuiteResult().getTestSuite().getId()
                      + "_" + testcaseResult.getTestCaseId();
              return !consolidatedResults.containsKey(key);
            }
    ).forEach(testcaseResult -> {
      String key = testcaseResult.getTestDeviceResult().getTestDeviceId()
              + "_" + testcaseResult.getTestSuiteResult().getTestSuite().getId()
              + "_" + testcaseResult.getTestCaseId();
      consolidatedResults.put(key, testcaseResult);
    });

    if (executionResult.getParentResult() != null)
      this.findConsolidatedTestCaseResults(executionResult.getParentResult().getId(), consolidatedResults);

  }
  public void export(TestCaseResult testCaseResult, XLSUtil wrapper) throws ResourceNotFoundException {
    wrapper.getWorkbook().setSheetName(wrapper.getWorkbook().getSheetIndex(wrapper.getSheet()),
            "Run result summary");
    setResultDetails(testCaseResult, wrapper);
    setTestCasesSummary(testCaseResult, wrapper);
    setDetailedTestCaseList(testCaseResult, wrapper);
  }

  private void setResultDetails(TestCaseResult testCaseResult, XLSUtil wrapper)
          throws ResourceNotFoundException {
    setHeading(wrapper, "Execution Details");
    setDetailsKeyValue("Test Plan Name", testCaseResult.getTestPlanResult().getTestPlan().getName(), wrapper);
    setDetailsKeyValue("Test Machine Name", testCaseResult.getTestDeviceResult().getTestDevice().getTitle(), wrapper);
    setDetailsKeyValue("Test Suite Name", testCaseResult.getTestSuite().getName(), wrapper);
    setDetailsKeyValue("Test Case Name", testCaseResult.getTestCase().getName(), wrapper);
    if (testCaseResult.getTestCase().getDescription() != null)
      setDetailsKeyValue("Description", testCaseResult.getTestCase().getDescription().replaceAll("\\&([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-fA-F]{1,6});|\\<.*?\\>", ""), wrapper);
    if (testCaseResult.getIteration() != null) {
      setDetailsKeyValue("Iteration", testCaseResult.getIteration(), wrapper);
    }

    setDetailsKeyValue("RunId", testCaseResult.getId().toString(), wrapper);
    setDetailsKeyValue("Build No", testCaseResult.getTestPlanResult().getBuildNo(), wrapper);
   // setDetailsKeyValue("Triggered By", userService.find(testCaseResult.getTestPlanResult().getExecutedBy()).getUserName(), wrapper);
    setDetailsKeyValue("Execution Start Time", testCaseResult.getTestPlanResult().getStartTime().toString(), wrapper);
    setDetailsKeyValue("Execution End Time", testCaseResult.getTestPlanResult().getEndTime() != null ? testCaseResult.getTestPlanResult().getEndTime().toString() : "-", wrapper);
    setDetailsKeyValue("Execution Result",
            testCaseResult.getTestPlanResult().getResult().getName(), wrapper);
    setDetailsKeyValue("Execution Message", testCaseResult.getTestPlanResult().getMessage(), wrapper);
  }

  private void setHeading(XLSUtil wrapper, String key) {
    wrapper.getDataRow(wrapper, wrapper.getNewRow());
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    CellStyle header = XLSUtil.getTableHeaderStyle(wrapper);
    row.setRowStyle(header);
    row.createCell(1).setCellValue(key);
    row.getCell(1).setCellStyle(header);
  }

  private void setDetailsKeyValue(String key, String value, XLSUtil wrapper) {
    Integer count = 0;
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    row.createCell(count).setCellValue(key);
    row.getCell(count).setCellStyle(XLSUtil.getSecondAlignStyle(wrapper));
    row.createCell(++count).setCellValue(value);
  }

  private void setTestCasesSummary(TestCaseResult testCaseResult, XLSUtil wrapper) {
    setHeading(wrapper, "Summary");
    Object[] keys = {"Total Test Steps", "Queued", "Passed", "Failed", "Aborted", "Not Executed", "Stopped"};
    Object[] counts = {testCaseResult.getTotalCount(), testCaseResult.getQueuedCount(),
            testCaseResult.getPassedCount(), testCaseResult.getFailedCount(), testCaseResult.getAbortedCount(),
            testCaseResult.getNotExecutedCount(),
            //testCaseResult.getPreRequisiteFailedCount(),
            testCaseResult.getStoppedCount()};
    setCellsHorizontally(wrapper, keys, true);
    setCellsHorizontally(wrapper, counts, false);
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

  private void setDetailedTestCaseList(TestCaseResult testCaseResult, XLSUtil wrapper) throws ResourceNotFoundException {
    setHeading(wrapper, "Test Steps List");
    String[] keys = {"Test Step", "Result", "Start Time", "End Time", "Visual Test Results"};
    setCellsHorizontally(wrapper, keys, true);
    List<TestStepResult> testStepResults = testStepResultService.findAllByTestCaseResultId(testCaseResult.getId());
    for (TestStepResult testStepResult : testStepResults) {
      String action;
      String testStepType = testStepResult.getStepDetails().getType().toString();
      if (testStepType.equals(TestStepType.STEP_GROUP.getId().toString())) {
        action = testCaseService.find(Long.valueOf(testStepResult.getStepDetails().getStepGroupId().toString())).getName();
      } else if (testStepType.equals(TestStepType.WHILE_LOOP.getId().toString())) {
        action = TestStepConditionType.LOOP_WHILE.getName();
      } else if (testStepType.equals(TestStepType.FOR_LOOP.getId().toString())) {
        StepResultForLoopMetadata loopData = testStepResult.getMetadata().getForLoop();
        String index = String.valueOf(loopData.getIndex());
        String testdata = loopData.getTestDataName();
        String iteration = loopData.getIteration();
        action = "Loop Iteration #" + index + " :: " + testdata + " - " + iteration;
      } else if (testStepType.equals(TestStepType.BREAK_LOOP.getId().toString())
              || testStepType.equals(TestStepType.CONTINUE_LOOP.getId().toString())
              || testStepType.equals(TestStepType.CUSTOM_FUNCTION.getId().toString())) {
        action = testStepResult.getStepDetails().getAction();
      } else if (testStepType.equals(TestStepType.REST_STEP.getId().toString())
              || testStepResult.getGroupResultId() != null || testStepResult.getParentResultId() != null) {
        if (testStepResult.getStepDetails().getConditionType() != null
                && testStepResult.getStepDetails().getConditionType().toString().equals(TestStepConditionType.CONDITION_ELSE.getId().toString())) {
          action = TestStepConditionType.CONDITION_ELSE.getName();
        } else {
          action = testStepResult.getStepDetails().getAction();
        }
      } else {
        StepResultMetadata metadata = testStepResult.getMetadata();
        if (metadata.getAction() != null)
          action = metadata.getAction();
        else
          action = testStepResult.getStepDetails().getAction();
      }
      Optional<StepResultScreenshotComparison> screenshotComparison = stepResultScreenshotComparisonService.findByTestStepResultId(testStepResult.getId());
      Object[] values = {action, testStepResult.getResult().getName(),
              testStepResult.getStartTime(), testStepResult.getEndTime(),
              screenshotComparison.isPresent() ? screenshotComparison.get().getDiffCoordinates() == null ||
                      screenshotComparison.get().getDiffCoordinates().equals("[]") ? "PASS" :
                      "FAIL" :"N/A" };
      setCellsHorizontally(wrapper, values, false);
    }
  }

}
