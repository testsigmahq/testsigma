/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestSuiteResultDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestSuiteResultMapper;
import com.testsigma.model.TestSuiteResult;
import com.testsigma.service.TestSuiteResultService;
import com.testsigma.specification.TestSuiteResultSpecificationsBuilder;
import com.testsigma.util.XLSUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@Log4j2
@RequestMapping(path = "/test_suite_results")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestSuiteResultsController {

  private final TestSuiteResultService testSuiteResultService;
  private final TestSuiteResultMapper testSuiteResultMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestSuiteResultDTO> index(TestSuiteResultSpecificationsBuilder builder, Pageable pageable) {
    log.info("Request /test_suite_results/");
    Specification<TestSuiteResult> spec = builder.build();
    Page<TestSuiteResult> testSuiteResults = testSuiteResultService.findAll(spec, pageable);
    List<TestSuiteResultDTO> testSuiteResultDTOS =
      testSuiteResultMapper.mapDTO(testSuiteResults.getContent());
    return new PageImpl<>(testSuiteResultDTOS, pageable, testSuiteResults.getTotalElements());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
  public TestSuiteResultDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Request /test_suite_results/" + id);
    TestSuiteResult testSuiteResult = testSuiteResultService.find(id);
    return testSuiteResultMapper.mapDTO(testSuiteResult);
  }

  @GetMapping(value = "/export/{id}")
  @PreAuthorize("hasPermission('RESULTS','READ')")
  public void exportRunResults(
          HttpServletRequest request,
          @PathVariable(value = "id") Long id,
          HttpServletResponse response) throws ResourceNotFoundException {
    TestSuiteResult testSuiteResult = testSuiteResultService.find(id);
    XLSUtil wrapper = new XLSUtil();
    testSuiteResultService.export(testSuiteResult, wrapper);
    wrapper.writeToStream(request, response, testSuiteResult.getTestSuite().getName());
  }
}
