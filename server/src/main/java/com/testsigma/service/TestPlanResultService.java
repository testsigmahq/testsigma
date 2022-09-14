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
import com.testsigma.event.EventType;
import com.testsigma.event.TestPlanResultEvent;
import com.testsigma.event.TestPlanResultNotificationEvent;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.*;
import com.testsigma.repository.TestPlanResultRepository;
import com.testsigma.util.XLSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class TestPlanResultService {

  private final TestCaseResultService testCaseResultService;
  private final TestPlanResultRepository testPlanResultRepository;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final ApplicationEventPublisher applicationEventPublisher;

  public TestPlanResult find(Long id) throws ResourceNotFoundException {
    return this.testPlanResultRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not " +
      "found " + "for id: " + id));
  }


  public TestPlanResult findByIdAndtestPlanId(Long id, Long testPlanId) {
    return this.testPlanResultRepository.findByIdAndTestPlanId(id, testPlanId);
  }

  public boolean findByReRunParentId(Long reRunParentId){
    return this.testPlanResultRepository.findByReRunParentId(reRunParentId) != null;
  }

  public TestPlanResult findByTestPlanIdAndStatusIsNot(Long testPlanId, StatusConstant status) {
    return this.testPlanResultRepository.findByTestPlanIdAndStatusIsNot(testPlanId, status);
  }

  public TestPlanResult create(TestPlanResult testPlanResult) {
    testPlanResult = this.testPlanResultRepository.save(testPlanResult);
    publishEvent(testPlanResult, EventType.CREATE);
    return testPlanResult;
  }

  public TestPlanResult update(TestPlanResult testPlanResult) {
    testPlanResult = this.testPlanResultRepository.save(testPlanResult);
    publishEvent(testPlanResult, EventType.UPDATE);
    return testPlanResult;
  }

  public TestPlanResult updateExecutionResult(ResultConstant maxResult, TestPlanResult testPlanResult) {
    String message = ResultConstant.SUCCESS.equals(maxResult) ? AutomatorMessages.MSG_EXECUTION_COMPLETED :
      (ResultConstant.STOPPED.equals(maxResult)) ? AutomatorMessages.MSG_TEST_PLAN_STOPPED :
        AutomatorMessages.MSG_EXECUTION_FAILURE;

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    testPlanResult.setResult(maxResult);
    testPlanResult.setStatus(StatusConstant.STATUS_COMPLETED);
    testPlanResult.setMessage(message);
    testPlanResult.setStartTime(ObjectUtils.defaultIfNull(testPlanResult.getStartTime(), currentTime));
    testPlanResult.setEndTime(currentTime);
    testPlanResult.setDuration(testPlanResult.getEndTime().getTime() - testPlanResult.getStartTime().getTime());
    testPlanResult = update(testPlanResult);
    updateResultCounts(testPlanResult);
    publishNotificationEvent(testPlanResult, EventType.UPDATE);
    return testPlanResult;
  }

  public void rerun(AbstractTestPlan testPlan, TestPlanResult testPlanResult) throws Exception {
    //Check if eligible for rerun. If its already a rerun(Manual or automatic) then rerun parentId will be referring to an existing run.
    if (isReRunEligible(testPlan, testPlanResult)) {
      log.info(String.format("Re-run Test Plan :- Execution - %s, ExecutionResult - %s,  ReRunType - %s",
        testPlan.getId(), testPlanResult.getId(), testPlan.getReRunType()));
      AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
      agentExecutionService.setTestPlan(testPlan);
      agentExecutionService.setIsReRun(Boolean.TRUE);
      agentExecutionService.setReRunType(testPlan.getReRunType());
      agentExecutionService.setParentTestPlanResultId(testPlanResult.getId());
      agentExecutionService.start();
    } else {
      log.info(String.format("Test Plan not eligible for re-run :- Execution - %s, ExecutionResult - %s,  ReRunType - %s",
        testPlan.getId(), testPlanResult.getId(), testPlan.getReRunType()));
    }
  }

  private boolean isReRunEligible(AbstractTestPlan testPlan, TestPlanResult testPlanResult) {
    return (testPlanResult.getStatus() == StatusConstant.STATUS_COMPLETED
      && testPlan.getReRunType() != ReRunType.NONE
      && testPlan.getReRunType() != null
      && testPlanResult.getReRunParentId() == null
      && testPlanResult.getResult() != ResultConstant.STOPPED
      && testPlanResult.getResult() != ResultConstant.SUCCESS
      && isReRunEligibleForDataDriven(testPlan, testPlanResult));
  }

  private boolean isReRunEligibleForDataDriven(AbstractTestPlan testPlan, TestPlanResult testPlanResult){
    if(ReRunType.ONLY_FAILED_ITERATIONS_IN_FAILED_TESTS.equals(testPlan.getReRunType())){
      List<TestCaseResult> failedTestCases = testCaseResultService.findAllByTestPlanResultIdAndResultIsNot(testPlanResult.getId(), ResultConstant.SUCCESS);
      if(failedTestCases.size() == 0)
        return false;
    }
    return true;
  }

  public void updateResultCounts(TestPlanResult testPlanResult) {
    this.testPlanResultRepository.updateTotalTestCaseResultsCount(testPlanResult.getId());
    this.testPlanResultRepository.updatePassedTestCaseResultsCount(testPlanResult.getId());
    this.testPlanResultRepository.updateFailedTestCaseResultsCount(testPlanResult.getId());
    this.testPlanResultRepository.updateAbortedTestCaseResultsCount(testPlanResult.getId());
    this.testPlanResultRepository.updateNotExecutedTestCaseResultsCount(testPlanResult.getId());
    this.testPlanResultRepository.updateQueuedTestCaseResultsCount(testPlanResult.getId());
    this.testPlanResultRepository.updateStoppedTestCaseResultsCount(testPlanResult.getId());
  }

  public Page<TestPlanResult> findAll(Specification<TestPlanResult> spec, Pageable pageable) {
    return this.testPlanResultRepository.findAll(spec, pageable);
  }

  public void updateVisualResult(TestPlanResult testPlanResult, boolean visualResult) {
    this.testPlanResultRepository.updateVisualResult(testPlanResult.getId(), visualResult);
  }

  public List<TestPlanResult> countOngoingEnvironmentResultsGroupByExecutionResult() {
    return this.testPlanResultRepository.countOngoingEnvironmentResultsGroupByTestPlanResult(
      Arrays.asList(StatusConstant.STATUS_IN_PROGRESS, StatusConstant.STATUS_PRE_FLIGHT, StatusConstant.STATUS_QUEUED));
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    TestPlanResult result = this.find(id);
    this.testPlanResultRepository.delete(result);
  }

  public void markTestPlanResultstatus(TestPlanResult testPlanResult, StatusConstant status, String message) {
    testPlanResult.setStatus(status);
    testPlanResult.setMessage(message);
    update(testPlanResult);
  }

  public void publishEvent(TestPlanResult testPlanResult, EventType eventType) {
    TestPlanResultEvent<TestPlanResult> event = createEvent(testPlanResult, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public void publishNotificationEvent(TestPlanResult testPlanResult, EventType eventType) {
    TestPlanResultNotificationEvent<TestPlanResult> event = createNotificationEvent(testPlanResult, eventType);
    log.info("Publishing event - " + event.toString());
    applicationEventPublisher.publishEvent(event);
  }

  public TestPlanResultEvent<TestPlanResult> createEvent(TestPlanResult testPlanResult, EventType eventType) {
    TestPlanResultEvent<TestPlanResult> event = new TestPlanResultEvent<>();
    event.setEventData(testPlanResult);
    event.setEventType(eventType);
    return event;
  }

  public TestPlanResultNotificationEvent<TestPlanResult> createNotificationEvent(TestPlanResult testPlanResult,
                                                                                 EventType eventType) {
    TestPlanResultNotificationEvent<TestPlanResult> event = new TestPlanResultNotificationEvent<>();
    event.setEventData(testPlanResult);
    event.setEventType(eventType);
    return event;
  }

  public List<TestPlanResultAndCount> countOngoingNonParallelEnvironmentResultsGroupByTestPlanResult() {
    return this.testPlanResultRepository.countOngoingNonParallelEnvironmentResultsGroupByTestPlanResult(
            Arrays.asList(StatusConstant.STATUS_IN_PROGRESS, StatusConstant.STATUS_PRE_FLIGHT));
  }

  public List<TestPlanResultAndCount> countOngoingParallelTestSuiteResultsGroupByTestPlanResult() {
    return this.testPlanResultRepository.countOngoingParallelTestSuiteResultsGroupByTestPlanResult(
            Arrays.asList(StatusConstant.STATUS_IN_PROGRESS, StatusConstant.STATUS_PRE_FLIGHT));
  }

  public List<TestPlanResultAndCount> countQueuedNonParallelEnvironmentResultsGroupByTestPlanResult() {
    return this.testPlanResultRepository.countOngoingNonParallelEnvironmentResultsGroupByTestPlanResult(
            Collections.singletonList(StatusConstant.STATUS_QUEUED));
  }

  public List<TestPlanResultAndCount> countQueuedParallelTestSuiteResultsGroupByTestPlanResult() {
    return this.testPlanResultRepository.countOngoingParallelTestSuiteResultsGroupByTestPlanResult(
            Collections.singletonList(StatusConstant.STATUS_QUEUED));
  }

  public TestPlanResult getFirstParentResult(TestPlanResult childResult){
    if(childResult.getParentResult() == null)
      return childResult;
    return getFirstParentResult(childResult.getParentResult());
  }

  public TestPlanResult findByIdAndTestPlanId(Long id, Long testPlanId) throws ResourceNotFoundException {
    return this.testPlanResultRepository.findByIdAndTestPlanId(id, testPlanId);
  }

  public void export(TestPlanResult testPlanResult, XLSUtil wrapper, boolean isConsolidatedReport) throws ResourceNotFoundException {
    int childCount = 0;
    List<TestPlanResult> allRunResults = new ArrayList<>();
    this.populateAllChildResults(testPlanResult,allRunResults);
    for(TestPlanResult result: allRunResults) {
      String sheetTitle;
      if (!isConsolidatedReport)
        sheetTitle = result.getReRunParentId() == null ? "Run Result" : "Re-Run " + ++childCount;
      else {
        sheetTitle = "Consolidated Result Summary";
      }
      wrapper.getWorkbook().setSheetName(wrapper.getWorkbook().getSheetIndex(wrapper.getSheet()), sheetTitle);
      wrapper.setCurrentRow(-1);
      setResultDetails(result, wrapper);
      setTestCasesSummary(result, wrapper, isConsolidatedReport);
      setDetailedTestCaseList(result, wrapper, isConsolidatedReport);
      if(isConsolidatedReport) {
        wrapper.getWorkbook().setSheetOrder("Consolidated Result Summary", 0);
        return;
      }
      wrapper.createSheet();
    }
    this.findConsolidatedResultByTestPlanId(testPlanResult);
    this.export(testPlanResult,wrapper,true);
  }

  private void findConsolidatedResultByTestPlanId(TestPlanResult testPlanResult) {
    TestPlanResult childResult = testPlanResult.getTestPlan().getLastRun();
    TestPlanResult tempChildResult = testPlanResult.getChildResult();
    setConsolidatedResults(testPlanResult,childResult);
    while (tempChildResult != null) {
      if(ReRunType.runFailedTestCases(tempChildResult.getReRunType())) {
        testPlanResult.setConsolidatedPassedCount(testPlanResult.getPassedCount() + tempChildResult.getPassedCount());

      }
      else {
        testPlanResult.setConsolidatedPassedCount(tempChildResult.getPassedCount());
        testPlanResult.setConsolidatedTotalTestcasesCount(tempChildResult.getTotalCount());
      }
      tempChildResult = tempChildResult.getChildResult();
    }
  }

  private void setConsolidatedResults(TestPlanResult testPlanResult, TestPlanResult childResult){
    testPlanResult.setConsolidatedMessage(childResult.getConsolidatedMessage());
    testPlanResult.setConsolidatedResult(childResult.getResult());
    testPlanResult.setConsolidatedTotalTestcasesCount(testPlanResult.getTotalCount());
    testPlanResult.setConsolidatedPassedCount(testPlanResult.getPassedCount());
    testPlanResult.setConsolidatedFailedCount(childResult.getFailedCount());
    testPlanResult.setConsolidatedAbortedCount(childResult.getAbortedCount());
    testPlanResult.setConsolidatedStoppedCount(childResult.getStoppedCount());
    testPlanResult.setConsolidatedNotExecutedCount(childResult.getNotExecutedCount());
    //testPlanResult.setConsolidatedPrerequisiteFailedCount(childResult.getPreRequisiteFailedCount());
    testPlanResult.setConsolidatedQueuedCount(childResult.getQueuedCount());
  }

  private void setDetailedTestCaseList(TestPlanResult testPlanResult, XLSUtil wrapper, boolean isConsolidated) throws ResourceNotFoundException {
    setHeading(wrapper, "Test Cases List");
    String[] keys = {"Test Case", "Test Suite", "Test Machine", "Result", "Start Time", "End Time", "Visual Test Results"};
    setCellsHorizontally(wrapper, keys, true);
    List<TestCaseResult> testCaseResults = new ArrayList<>();
    if (isConsolidated)
      testCaseResults = testCaseResultService.findConsolidatedTestCaseResultsByExecutionResultId(testPlanResult.getId());
    else
      testCaseResults = testCaseResultService.findAllByTestPlanResultId(testPlanResult.getId());
    for (TestCaseResult testCaseResult : testCaseResults) {
      Object[] values = {testCaseResult.getTestCase().getName(), testCaseResult.getTestSuite().getName(),
              testCaseResult.getTestDeviceResult().getTestDevice().getTitle(),
              testCaseResult.getResult().getName(), testCaseResult.getStartTime(),
              testCaseResult.getEndTime(), testCaseResult.getIsVisuallyPassed() == null ? "N/A" : testCaseResult.getIsVisuallyPassed() ? "PASS" :"FAIL"};
      setCellsHorizontally(wrapper, values, false);
    }
  }
  public void populateAllChildResults(TestPlanResult testPlanResult, List<TestPlanResult> allResults){
    if(testPlanResult != null){
      allResults.add(testPlanResult);
      populateAllChildResults(testPlanResult.getChildResult(),allResults);
    }
  }

  private void setResultDetails(TestPlanResult testPlanResult, XLSUtil wrapper)
          throws ResourceNotFoundException {
    setHeading(wrapper, "TestPlan Details");
    setDetailsKeyValue("Test Plan Name", testPlanResult.getTestPlan().getName(),wrapper);
    if (testPlanResult.getTestPlan().getDescription() != null)
      setDetailsKeyValue("Description", testPlanResult.getTestPlan().getDescription().replaceAll("\\&([a-z0-9]+|#[0-9]{1,6}|#x[0-9a-fA-F]{1,6});|\\<.*?\\>", ""), wrapper);
    setDetailsKeyValue("RunId", testPlanResult.getId().toString(), wrapper);
    setDetailsKeyValue("Build No", testPlanResult.getBuildNo(), wrapper);
    //setDetailsKeyValue("Triggered By", userService.find(testPlanResult.getExecutedBy()).getUserName(), wrapper);
    setDetailsKeyValue("TestPlan Start Time", testPlanResult.getStartTime().toString(), wrapper);
    setDetailsKeyValue("TestPlan End Time", testPlanResult.getEndTime() != null ? testPlanResult.getEndTime().toString() : "-", wrapper);
    setDetailsKeyValue("TestPlan Result", testPlanResult.getResult().getName(), wrapper);
    setDetailsKeyValue("TestPlan Message", testPlanResult.getMessage(), wrapper);
  }

  private void setDetailsKeyValue(String key, String value, XLSUtil wrapper) {
    Integer count = 0;
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    row.createCell(count).setCellValue(key);
    row.getCell(count).setCellStyle(XLSUtil.getSecondAlignStyle(wrapper));
    row.createCell(++count).setCellValue(value);
  }

  private void setHeading(XLSUtil wrapper, String key) {
    if (wrapper.getCurrentRow() != -1)
      wrapper.getDataRow(wrapper, wrapper.getNewRow());
    Row row = wrapper.getDataRow(wrapper, wrapper.getNewRow());
    CellStyle header = XLSUtil.getTableHeaderStyle(wrapper);
    row.setRowStyle(header);
    row.createCell(1).setCellValue(key);
    row.getCell(1).setCellStyle(header);
  }

  private void setTestCasesSummary(TestPlanResult testPlanResult, XLSUtil wrapper, Boolean isConsolidated) {
    setHeading(wrapper, "Summary");
    Object[] keys = {"Total Test Cases", "Queued", "Passed", "Failed", "Aborted", "Not Executed", "Stopped"};
    Object[] counts;
    if(isConsolidated)
      counts = getConsolidatedResultCounts(testPlanResult);
    else
      counts = getResultCounts(testPlanResult);
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

  private Object[] getResultCounts(TestPlanResult testPlanResult){
    return new Object[]{testPlanResult.getTotalCount(), testPlanResult.getQueuedCount(),
            testPlanResult.getPassedCount(), testPlanResult.getFailedCount(), testPlanResult.getAbortedCount(),
            testPlanResult.getNotExecutedCount(),
            //testPlanResult.getPreRequisiteFailedCount(),
            testPlanResult.getStoppedCount()};
  }

  private Object[] getConsolidatedResultCounts(TestPlanResult testPlanResult){
    return new Object[]{testPlanResult.getConsolidatedTotalTestcasesCount(),
                        testPlanResult.getConsolidatedQueuedCount(),
                        testPlanResult.getConsolidatedPassedCount(),
                        testPlanResult.getConsolidatedFailedCount(),
                        testPlanResult.getConsolidatedAbortedCount(),
                        testPlanResult.getConsolidatedNotExecutedCount(),
                        testPlanResult.getConsolidatedPrerequisiteFailedCount(),
                        testPlanResult.getConsolidatedStoppedCount()
    };
  }


}
