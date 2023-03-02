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
import com.testsigma.mapper.ForLoopConditionsMapper;
import com.testsigma.mapper.TestStepResultMapper;
import com.testsigma.model.*;
import com.testsigma.repository.TestStepResultRepository;
import com.testsigma.web.request.*;
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
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
public class TestStepResultService {
  private final TestStepResultRepository testStepResultRepository;
  private final TestStepResultMapper testStepResultMapper;
  private final TestDataProfileService testDataProfileService;
  private final SuggestionResultMappingService suggestionResultMappingService;
  private final TestCaseResultService testCaseResultService;
  private final ForLoopConditionsMapper forLoopConditionsMapper;

  public Page<TestStepResult> findAll(Specification<TestStepResult> spec, Pageable pageable) {
    return this.testStepResultRepository.findAll(spec, pageable);
  }

  public List<TestStepResult> findAllByTestCaseResultIdAndScreenshotNameIsNotNull(Long testCaseResultId) {
    return this.testStepResultRepository.findAllByTestCaseResultIdAndScreenshotNameIsNotNull(testCaseResultId);
  }

  public List<TestStepResult> findAllByTestCaseResultIdAndScreenshotNameIsNotNullAndVisualEnabledIsTrue(Long testCaseResultId) {
    return this.testStepResultRepository.findAllByTestCaseResultIdAndScreenshotNameIsNotNullAndVisualEnabledIsTrue(testCaseResultId);
  }

  public TestStepResult create(TestStepResult testStepResult) {
    return testStepResultRepository.save(testStepResult);
  }

  public TestStepResult find(Long id) {
    return testStepResultRepository.findById(id).get();
  }

  public TestStepResult update(TestStepResult testStepResult) {
    return testStepResultRepository.save(testStepResult);
  }

  public Integer deleteByTestCaseResultIdAndEnvironmentResultId(Long testCaseResultId, Long environmentResultId) {
    return testStepResultRepository
      .deleteByTestCaseResultIdAndEnvironmentResultId(testCaseResultId, environmentResultId);
  }

  public void createTestCaseSteps(TestCaseResultRequest testCaseResultRequest, TestData testData,
                                  TestDataSet testDataSet) throws UnsupportedEncodingException {
    boolean isTestDataUpdated;

    List<TestStepResultRequest> testCaseStepResultList = testCaseResultRequest.getTestCaseStepResults();

    isTestDataUpdated = createTestCaseSteps(testCaseStepResultList, testDataSet, null, new HashMap<>());
    if (isTestDataUpdated) {
      setTestDataSet(testDataSet, testData);
      testDataProfileService.update(testData);
    }
  }

  private void setTestDataSet(TestDataSet testDataSet, TestData testData) {
    int index = 0;
    for (TestDataSet set : testData.getTempTestData()) {
      if (set.getName().equals(testDataSet.getName())) {
        break;
      }
      index++;
    }
    List<TestDataSet> sets = testData.getData();
    sets.set(index, testDataSet);
    testData.setData(sets);
    testData.setTempTestData(sets);
  }

  public boolean createTestCaseSteps(List<TestStepResultRequest> testCaseStepResultList, TestDataSet testDataSet,
                                     Long groupResultId, Map<Long, Long> condStepsMap)
    throws UnsupportedEncodingException {
    boolean updateTestData = false;
    for (TestStepResultRequest testStepResultRequest : testCaseStepResultList) {
      boolean updated = createTestCaseStep(testStepResultRequest, testDataSet, groupResultId, condStepsMap);
      updateTestData = updateTestData || updated;
    }
    return updateTestData;

  }

  private boolean createTestCaseStep(TestStepResultRequest testCaseStepResult,
                                     com.testsigma.model.TestDataSet testDataSet,
                                     Long groupResultId, Map<Long, Long> condStepsMap)
    throws UnsupportedEncodingException {
    boolean updateTestData = false;

    if (TestStepConditionType.LOOP_FOR == testCaseStepResult.getConditionType()) {
      StepResultForLoopMetadataRequest loopData = new StepResultForLoopMetadataRequest();
      loopData.setIteration(testCaseStepResult.getIteration());
      loopData.setIndex(testCaseStepResult.getIndex());
      loopData.setTestDataName(testCaseStepResult.getTestDataProfileName());
      loopData.setForLoopCondition(testCaseStepResult.getForLoopCondition());
      testCaseStepResult.getMetadata().setForLoop(loopData);
    } else if (TestStepConditionType.LOOP_WHILE == testCaseStepResult.getConditionType()) {
      StepResultWhileLoopMetadataRequest loopData = new StepResultWhileLoopMetadataRequest();
      loopData.setIndex(testCaseStepResult.getIndex());
      testCaseStepResult.getMetadata().setWhileLoop(loopData);
    }

    testCaseStepResult.setGroupResultId(groupResultId);
    Long parentResultId = getParentResultStepId(condStepsMap, testCaseStepResult);
    testCaseStepResult.setParentResultId(parentResultId);
    checkMetaMaxSize(testCaseStepResult);

    log.info("Create a test step result object : " + testCaseStepResult);
    TestStepResult testStepResult = null;
    testStepResult = testStepResultMapper.map(testCaseStepResult);
    testStepResult = testStepResultRepository.save(testStepResult);

    Long stepResultId = testStepResult.getId();
    testCaseStepResult.setId(stepResultId);

    if (testCaseStepResult.getConditionType() != null) {
      condStepsMap.put(testStepResult.getStepId(), stepResultId);
    }

    if (testDataSet != null) {
      updateTestData = updateTestDataSet(testDataSet, testCaseStepResult.getOutputData());
    }

    if (TestStepType.FOR_LOOP == testCaseStepResult.getTestCaseStepType()) {
      createTestCaseSteps(testCaseStepResult.getStepResults(), testDataSet, groupResultId, condStepsMap);
    } else if (TestStepType.STEP_GROUP == testCaseStepResult.getTestCaseStepType()) {
      boolean updated =
        createTestCaseSteps(testCaseStepResult.getStepResults(), testDataSet, stepResultId, condStepsMap);

      if (!updateTestData) {
        updateTestData = updated;
      }
    } else if (TestStepType.WHILE_LOOP == testCaseStepResult.getTestCaseStepType() || TestStepConditionType.LOOP_WHILE == testCaseStepResult.getConditionType()) {
      createTestCaseSteps(testCaseStepResult.getStepResults(), testDataSet, groupResultId, condStepsMap);
    }
    for (SuggestionEngineResultRequest suggestionEngineResultRequest : testCaseStepResult.getSuggestionResults()) {
      this.suggestionResultMappingService.create(suggestionEngineResultRequest, testStepResult);
    }
    return updateTestData;
  }

  private Long getParentResultStepId(Map<Long, Long> condStepsMap, TestStepResultRequest testCaseStepResult) {
    Long parentResultId = condStepsMap.get(testCaseStepResult.getParentId());
    if (parentResultId == null && testCaseStepResult.getParentId() != null) {
      log.debug("ParentResultId missing in current batch so fetching from database if its saved in previous batch");
      Optional<TestStepResult> stepResult = this.findByTestCaseResultIdAndStepId(testCaseStepResult.getTestCaseResultId(), testCaseStepResult.getParentId());
      parentResultId = stepResult.map(TestStepResult::getId).orElse(null);
      condStepsMap.put(testCaseStepResult.getParentId(), parentResultId);
    }
    return parentResultId;
  }

  private Optional<TestStepResult> findByTestCaseResultIdAndStepId(Long testCaseResultId, Long testCaseStepId) {
    return this.testStepResultRepository.findFirstByTestCaseResultIdAndStepIdOrderByIdDesc(testCaseResultId, testCaseStepId);
  }

  private boolean updateTestDataSet(TestDataSet testDataSet, Map<String, String> outputData) {
    boolean isUpdated = false;
    for (Map.Entry<String, String> entry : outputData.entrySet()) {
      if (testDataSet.getData().has(entry.getKey())) {
        isUpdated = true;
        testDataSet.getData().put(entry.getKey(), entry.getValue());
      }
    }
    return isUpdated;
  }

  private void checkMetaMaxSize(TestStepResultRequest testCaseStepResult) {
    String metaData = new ObjectMapperService().convertToJson(testCaseStepResult.getMetadata());
    byte[] bytes = metaData.getBytes(StandardCharsets.UTF_8);
    //log.debug("Step metadata::"+testCaseStepResult.getMetadata());

    if (!org.apache.commons.lang3.StringUtils.isEmpty(metaData) && bytes.length >= 65535 * 10 && testCaseStepResult.getMetadata() != null
      && testCaseStepResult.getMetadata().getRestResult() != null) {
      testCaseStepResult.getMetadata().getRestResult().setContent("{ \"error\" : \"" + AutomatorMessages.MSG_RESPONSE_SIZE_EXCEEDS + "\"}");
    }

  }

  public void updateStepGroupResult(ResultConstant result, String message, Timestamp startTime, Timestamp endTime,
                                    Long groupResultId) {
    testStepResultRepository
      .updateStepGroupResult(result, message, startTime, endTime, endTime.getTime() - startTime.getTime(),
        ResultConstant.QUEUED, groupResultId);
  }

  public void updateTestStepResultUp(TestStepResult result) throws Exception {
    TestCaseResult testcaseResult = testCaseResultService.find(result.getTestCaseResultId());
    if (result.getResult().equals(ResultConstant.QUEUED)) {
      testcaseResult.setResult(ResultConstant.QUEUED);
      testcaseResult.setStatus(StatusConstant.STATUS_PRE_FLIGHT);
      testcaseResult.setMessage(result.getMessage());
      testcaseResult.setStartTime(null);
      testcaseResult.setEndTime(null);
      testcaseResult.setDuration(0L);
      testCaseResultService.update(testcaseResult);
      testCaseResultService.updateParentResult(testcaseResult);
      return;
    }

    Integer pendingTestStepResultCount = testStepResultRepository.countAllByTestCaseResultIdAndResult(
      result.getTestCaseResultId(), ResultConstant.QUEUED);

    if (pendingTestStepResultCount == 0) {
      ResultConstant maxResult = testStepResultRepository.findMaxResultByTestCaseResultId(result.getTestCaseResultId());
      Timestamp startTime = testStepResultRepository.findMinimumStartTimeByTestCaseResultId(result.getTestCaseResultId());
      Timestamp endTime = testStepResultRepository.findMaximumEndTimeByTestCaseResultId(result.getTestCaseResultId());
      startTime = ObjectUtils.defaultIfNull(startTime, result.getStartTime());
      endTime = ObjectUtils.defaultIfNull(endTime, result.getEndTime());
      testcaseResult.setResult(maxResult);
      testcaseResult.setMessage(result.getMessage());
      testcaseResult.setStartTime(startTime);
      testcaseResult.setEndTime(endTime);
      testcaseResult.setDuration(startTime.getTime() - endTime.getTime());
      testcaseResult.setStatus(StatusConstant.STATUS_COMPLETED);
      testCaseResultService.update(testcaseResult);
      testCaseResultService.updateParentResult(testcaseResult);
    }
  }

  public void updateStepGroupResult(TestStepResult result) throws Exception {
    TestStepResult stepResult = find(result.getGroupResultId());
    if (result.getResult() == ResultConstant.QUEUED) {
      stepResult.setResult(ResultConstant.QUEUED);
      stepResult.setMessage(result.getMessage());
      stepResult.setStartTime(null);
      stepResult.setEndTime(null);
      stepResult.setDuration(0L);
      update(stepResult);
    }

    Integer pendingTestStepResultCount = testStepResultRepository.countAllBygroupResultIdAndResult(
      result.getGroupResultId(), ResultConstant.QUEUED);

    if (pendingTestStepResultCount == 0) {
      ResultConstant maxResult = testStepResultRepository.findMaxResultBygroupResultId(result.getGroupResultId());
      Timestamp startTime = testStepResultRepository.findMinimumStartTimeBygroupResultId(result.getGroupResultId());
      Timestamp endTime = testStepResultRepository.findMaximumEndTimeBygroupResultId(result.getGroupResultId());
      startTime = ObjectUtils.defaultIfNull(startTime, result.getStartTime());
      endTime = ObjectUtils.defaultIfNull(endTime, result.getEndTime());
      stepResult.setResult(maxResult);
      stepResult.setMessage(result.getMessage());
      stepResult.setStartTime(startTime);
      stepResult.setEndTime(endTime);
      stepResult.setDuration(startTime.getTime() - endTime.getTime());
      update(stepResult);
      updateTestStepResultUp(stepResult);
    }
  }

  public void stopIncompleteTestStepResults(ResultConstant result, String message, Long duration, Timestamp startTime, Timestamp endTime, Long environmentRunId) {
    testStepResultRepository.stopIncompleteTestStepResults(result, message, duration, startTime,
      endTime, environmentRunId, ResultConstant.QUEUED);
  }

  public List<TestStepResult> findAllByTestCaseResultId(Long id) {
    return testStepResultRepository.findAllByTestCaseResultId(id);
  }
}
