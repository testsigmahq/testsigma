/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestCaseResultDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestCaseResultMapper;
import com.testsigma.model.TestCaseResult;
import com.testsigma.service.TestCaseResultService;
import com.testsigma.specification.TestCaseResultSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/test_case_results")
public class TestCaseResultsController {

  private final TestCaseResultService testCaseResultService;
  private final TestCaseResultMapper testCaseResultMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestCaseResultDTO> index(TestCaseResultSpecificationsBuilder builder, Pageable pageable) {
    log.info("Request /test_case_results/");
    Specification<TestCaseResult> spec = builder.build();
    Page<TestCaseResult> testCaseResults = testCaseResultService.findAll(spec, pageable);
    List<TestCaseResultDTO> testSuiteResultDTOs =
      testCaseResultMapper.mapDTO(testCaseResults.getContent());
    return new PageImpl<>(testSuiteResultDTOs, pageable, testCaseResults.getTotalElements());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
  public TestCaseResultDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Request /test_case_results/" + id);
    TestCaseResult testCaseResult = testCaseResultService.find(id);
    if(testCaseResult.getMessage().contains("architecture is unsupported")) {
      testCaseResult.setMessage("Architecture is unsupported for the selected application. Please select the correct application file or change the device architecture");
    }
    return testCaseResultMapper.mapDTO(testCaseResult);
  }
}
