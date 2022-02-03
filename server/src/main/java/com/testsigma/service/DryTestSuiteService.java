package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.DryTestSuite;
import com.testsigma.model.SuiteTestCaseMapping;
import com.testsigma.model.TestCase;
import com.testsigma.repository.DryTestSuiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Autowired, @Lazy}))
@Log4j2
public class DryTestSuiteService {
  protected final TestCaseService testCaseService;
  private final DryTestSuiteRepository repository;
  private final SuiteTestCaseMappingService suiteTestCaseMappingService;

  public DryTestSuite create(DryTestSuite testSuite) throws ResourceNotFoundException {
    testSuite = this.repository.save(testSuite);
    TestCase testCase = testCaseService.find(testSuite.getTestCaseId());
    handleSuiteMapping(testSuite, testCase, 0);
    return testSuite;
  }

  private int handleSuiteMapping(DryTestSuite testSuite, TestCase testCase, int position) {
    TestCase preRequisiteCase = testCase.getPreRequisiteCase();
    if (preRequisiteCase != null)
      position = handleSuiteMapping(testSuite, preRequisiteCase, position);
    SuiteTestCaseMapping suiteTestCaseMapping = new SuiteTestCaseMapping();
    position++;
    suiteTestCaseMapping.setSuiteId(testSuite.getId());
    suiteTestCaseMapping.setTestCaseId(testCase.getId());
    suiteTestCaseMapping.setPosition(position);
    this.suiteTestCaseMappingService.add(suiteTestCaseMapping);
    return position;
  }


}
