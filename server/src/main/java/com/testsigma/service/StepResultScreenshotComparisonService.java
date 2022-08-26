/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.StepResultScreenshotComparison;
import com.testsigma.model.TestCaseResult;
import com.testsigma.repository.StepResultScreenshotComparisonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StepResultScreenshotComparisonService {
  private final StepResultScreenshotComparisonRepository repository;
  private final TestCaseResultService testCaseResultService;
  private final TestSuiteResultService testSuiteResultService;

  public Page<StepResultScreenshotComparison> findAll(Specification<StepResultScreenshotComparison> spec, Pageable pageable) {
    return this.repository.findAll(spec, pageable);
  }

  public StepResultScreenshotComparison find(Long id) throws ResourceNotFoundException {
    return this.repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Missing with id:" + id));
  }

  public StepResultScreenshotComparison update(StepResultScreenshotComparison comparison) {
    return this.repository.save(comparison);
  }

  public StepResultScreenshotComparison create(StepResultScreenshotComparison comparison) {
    return this.repository.save(comparison);
  }

  public List<StepResultScreenshotComparison> findAllByTestCaseResultIdAndSimilarityScoreIsNull(Long testCaseResultId) {
    return this.repository.findAllByTestCaseResultIdAndSimilarityScoreIsNull(testCaseResultId);
  }

  public List<StepResultScreenshotComparison> findAllByTestCaseResultIdAndDiffCoordinatesNot(Long testCaseResultId, String diffCorOrdinates) {
    return this.repository.findAllByTestCaseResultIdAndDiffCoordinatesNot(testCaseResultId, diffCorOrdinates);
  }

  public void propagateVisualResult(StepResultScreenshotComparison resultScreenshotComparison) throws ResourceNotFoundException {
    List<StepResultScreenshotComparison> failedList = findAllByTestCaseResultIdAndDiffCoordinatesNot(resultScreenshotComparison.getTestCaseResultId(), "[]");
    TestCaseResult testCaseResult = resultScreenshotComparison.getTestCaseResult();
    testCaseResultService.updateVisualResult(testCaseResult, failedList.isEmpty());
    if (!failedList.isEmpty()) {
      testCaseResultService.propagateVisualResult(testCaseResult);
    }
  }

  Optional<StepResultScreenshotComparison> findByTestStepResultId(Long testStepResultId){
    return this.repository.findByTestStepResultId(testStepResultId);
  }
}
