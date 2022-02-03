/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.mapper.TestCaseDataDrivenResultMapper;
import com.testsigma.model.TestCaseDataDrivenResult;
import com.testsigma.model.TestDataSet;
import com.testsigma.repository.TestCaseDataDrivenResultRepository;
import com.testsigma.web.request.TestCaseResultRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCaseDataDrivenResultService {

  private final TestCaseDataDrivenResultRepository testCaseDataDrivenResultRepository;
  private final TestCaseDataDrivenResultMapper testCaseDataDrivenResultMapper;

  public TestCaseDataDrivenResult find(Long id) {
    return testCaseDataDrivenResultRepository.findById(id).orElse(null);
  }

  public void deleteByIterationResultId(Long deleteByTestCaseResultId) {
    testCaseDataDrivenResultRepository.deleteByIterationResultId(deleteByTestCaseResultId);
  }

  public TestCaseDataDrivenResult create(TestCaseResultRequest testCaseResultRequest, TestDataSet testDataSet) {
    TestCaseDataDrivenResult testCaseDataDrivenResult = testCaseDataDrivenResultMapper.map(testCaseResultRequest);
    testCaseDataDrivenResult.setTestData(new ObjectMapperService().convertToJson(testDataSet));
    return testCaseDataDrivenResultRepository.save(testCaseDataDrivenResult);
  }


  public Page<TestCaseDataDrivenResult> findAll(Specification<TestCaseDataDrivenResult> spec, Pageable pageable) {
    return testCaseDataDrivenResultRepository.findAll(spec, pageable);
  }

  public TestCaseDataDrivenResult create(TestCaseDataDrivenResult testCaseDataDrivenResult) {
    return this.testCaseDataDrivenResultRepository.save(testCaseDataDrivenResult);
  }
}
