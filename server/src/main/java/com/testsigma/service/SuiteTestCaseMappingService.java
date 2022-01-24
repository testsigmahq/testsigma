/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.model.AbstractTestSuite;
import com.testsigma.model.SuiteTestCaseMapping;
import com.testsigma.model.TestCase;
import com.testsigma.repository.SuiteTestCaseMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SuiteTestCaseMappingService {

  private final SuiteTestCaseMappingRepository suiteTestCaseMappingRepository;

  public Optional<SuiteTestCaseMapping> findFirstByTestSuiteAndTestCase(AbstractTestSuite testSuite, TestCase testCase) {
    return suiteTestCaseMappingRepository.findFirstByTestSuiteAndTestCase(testSuite, testCase);
  }

  public List<SuiteTestCaseMapping> findAllBySuiteId(Long id) {
    return this.suiteTestCaseMappingRepository.findAllBySuiteId(id);
  }

  public SuiteTestCaseMapping add(SuiteTestCaseMapping suiteTestCaseMapping) {
    return this.suiteTestCaseMappingRepository.save(suiteTestCaseMapping);
  }

  public SuiteTestCaseMapping update(SuiteTestCaseMapping suiteTestCaseMapping) {
    return this.suiteTestCaseMappingRepository.save(suiteTestCaseMapping);
  }

  public Boolean deleteAll(List<SuiteTestCaseMapping> deletableMaps) {
    this.suiteTestCaseMappingRepository.deleteAll(deletableMaps);
    return true;
  }
}
