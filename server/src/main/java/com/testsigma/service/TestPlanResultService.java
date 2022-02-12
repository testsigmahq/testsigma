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
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class TestPlanResultService {

  private final TestPlanResultRepository testPlanResultRepository;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;
  private final ApplicationEventPublisher applicationEventPublisher;

  public TestPlanResult find(Long id) throws ResourceNotFoundException {
    return this.testPlanResultRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Resource not " +
      "found " + "for id: " + id));
  }


  public TestPlanResult findByIdAndTestPlanId(Long id, Long testPlanId) {
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
      && testPlanResult.getResult() != ResultConstant.SUCCESS);
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
    return this.testPlanResultRepository.countOngoingEnvironmentResultsGroupByExecutionResult(
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
}
