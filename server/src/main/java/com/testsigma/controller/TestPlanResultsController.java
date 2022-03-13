/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.TestPlanResultDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestPlanResultMapper;
import com.testsigma.model.*;
import com.testsigma.service.AgentExecutionService;
import com.testsigma.service.TestPlanResultService;
import com.testsigma.service.TestPlanService;
import com.testsigma.specification.TestPlanResultSpecificationsBuilder;
import com.testsigma.web.request.TestPlanResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping(path = "/test_plan_results", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestPlanResultsController {
  private final TestPlanResultService testPlanResultService;
  private final TestPlanResultMapper testPlanResultMapper;
  private final TestPlanService testPlanService;
  private final ObjectFactory<AgentExecutionService> agentExecutionServiceObjectFactory;

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestPlanResultDTO> index(TestPlanResultSpecificationsBuilder builder, Pageable pageable) {
    log.info("Request /test_plan_results/");
    Specification<TestPlanResult> spec = builder.build();
    Page<TestPlanResult> testPlanResults = testPlanResultService.findAll(spec, pageable);
    List<TestPlanResultDTO> testPlanResultDTOS =
      testPlanResultMapper.map(testPlanResults.getContent());
    return new PageImpl<>(testPlanResultDTOS, pageable, testPlanResults.getTotalElements());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
  public TestPlanResultDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Request /test_plan_results/" + id);
    TestPlanResult testPlanResult = testPlanResultService.find(id);
    return testPlanResultMapper.mapTo(testPlanResult);
  }

  @RequestMapping(method = RequestMethod.POST)
  public TestPlanResultDTO create(@RequestBody TestPlanResultRequest testPlanResultRequest) throws Exception {
    log.info("Create Request /test_plan_results/ with data::" + testPlanResultRequest);
    TestPlan testPlan = this.testPlanService.find(testPlanResultRequest.getTestPlanId());
    AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
    agentExecutionService.setTestPlan(testPlan);
    JSONObject runTimeData = new JSONObject();
    if (testPlanResultRequest.getRuntimeData() != null) {
      JSONObject runtimeDataObject = new JSONObject(testPlanResultRequest.getRuntimeData());
      runTimeData.put("runtime_data", runtimeDataObject);
    }
    agentExecutionService.setIsReRun(testPlanResultRequest.getIsReRun());
    runTimeData.put("build_number", testPlanResultRequest.getBuildNo());
    agentExecutionService.setRunTimeData(runTimeData);
    if (testPlanResultRequest.getReRunType() != null
      && testPlanResultRequest.getReRunType() != ReRunType.NONE
      && testPlanResultRequest.getParenttestPlanResultId() != null) {
      agentExecutionService.setReRunType(testPlanResultRequest.getReRunType());
      agentExecutionService.setParentTestPlanResultId(testPlanResultRequest.getParenttestPlanResultId());

    }
    agentExecutionService.start();
    return testPlanResultMapper.mapTo(agentExecutionService.getTestPlanResult());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
  public TestPlanResultDTO update(@RequestBody TestPlanResultRequest testPlanResultRequest,
                                  @PathVariable(value = "id") Long id) throws Exception {
    log.info("Update Request /test_plan_results/" + id + " with data::" + testPlanResultRequest);
    TestPlanResult testPlanResult = testPlanResultService.find(id);
    if (testPlanResultRequest.getResult() == ResultConstant.STOPPED) {
      AbstractTestPlan testPlan = testPlanResult.getTestPlan();
      if (testPlan == null)
        testPlan = testPlanResult.getDryTestPlan();
      AgentExecutionService agentExecutionService = agentExecutionServiceObjectFactory.getObject();
      agentExecutionService.setTestPlan(testPlan);
      agentExecutionService.stop();
    }
    testPlanResultMapper.merge(testPlanResultRequest, testPlanResult);
    testPlanResult = testPlanResultService.update(testPlanResult);
    this.testPlanResultService.updateResultCounts(testPlanResult);
    return testPlanResultMapper.mapTo(testPlanResult);
  }

  @RequestMapping(value = {"/running-counts"}, method = RequestMethod.GET)
  public Page<TestPlanResultDTO> counts() {
    List<TestPlanResult> ongoingTestPlans = testPlanResultService
      .countOngoingEnvironmentResultsGroupByExecutionResult();

    List<TestPlanResultAndCount> ongoingNonParallelEnvironmentResultCounts = testPlanResultService
            .countOngoingNonParallelEnvironmentResultsGroupByTestPlanResult();
    List<TestPlanResultAndCount> ongoingParallelTestSuiteResultCounts = testPlanResultService
            .countOngoingParallelTestSuiteResultsGroupByTestPlanResult();

    List<TestPlanResultAndCount> queuedNonParallelEnvironmentResultCounts = testPlanResultService
            .countQueuedNonParallelEnvironmentResultsGroupByTestPlanResult();
    List<TestPlanResultAndCount> queuedParallelTestSuiteResultCounts = testPlanResultService
            .countQueuedParallelTestSuiteResultsGroupByTestPlanResult();    

    Map<Long, TestPlanResult> testPlanResultMap = new HashMap<>();
    for (TestPlanResult er : ongoingTestPlans) {
      testPlanResultMap.put(er.getId(), er);
    }

    for (TestPlanResultAndCount ec : ongoingNonParallelEnvironmentResultCounts) {
      TestPlanResult er = testPlanResultMap.get(ec.getTestPlanResultId());
      if (er != null)
        er.setTotalRunningCount(er.getTotalRunningCount() + ec.getResultCount());
    }

    for (TestPlanResultAndCount tc : ongoingParallelTestSuiteResultCounts) {
      TestPlanResult er = testPlanResultMap.get(tc.getTestPlanResultId());
      if (er != null)
        er.setTotalRunningCount(er.getTotalRunningCount() + tc.getResultCount());
    }

    for (TestPlanResultAndCount ec : queuedNonParallelEnvironmentResultCounts) {
      TestPlanResult er = testPlanResultMap.get(ec.getTestPlanResultId());
      if (er != null)
        er.setTotalQueuedCount(er.getTotalQueuedCount() + ec.getResultCount());
    }

    for (TestPlanResultAndCount tc : queuedParallelTestSuiteResultCounts) {
      TestPlanResult er = testPlanResultMap.get(tc.getTestPlanResultId());
      if (er != null)
        er.setTotalQueuedCount(er.getTotalQueuedCount() + tc.getResultCount());
    }


    return new PageImpl<>(testPlanResultMapper.map(ongoingTestPlans));
  }

  @DeleteMapping(value = "/{id}")
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    testPlanResultService.destroy(id);
  }
}
