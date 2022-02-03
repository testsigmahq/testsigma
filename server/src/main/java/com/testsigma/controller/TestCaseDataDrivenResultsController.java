/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.TestCaseDataDrivenResultDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestCaseDataDrivenResultMapper;
import com.testsigma.model.TestCaseDataDrivenResult;
import com.testsigma.service.TestCaseDataDrivenResultService;
import com.testsigma.specification.TestCaseDataDrivenResultSpecificationsBuilder;
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
@RequestMapping(path = "/test_case_data_driven_results")
public class TestCaseDataDrivenResultsController {

  private final TestCaseDataDrivenResultService testCaseDataDrivenResultService;
  private final TestCaseDataDrivenResultMapper testCaseDataDrivenResultMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestCaseDataDrivenResultDTO> index(TestCaseDataDrivenResultSpecificationsBuilder builder,
                                                 Pageable pageable) {
    log.info("Request /test_case_data_driven_results/");
    Specification<TestCaseDataDrivenResult> spec = builder.build();
    Page<TestCaseDataDrivenResult> testCaseDataDrivenResults = testCaseDataDrivenResultService.findAll(spec, pageable);
    List<TestCaseDataDrivenResultDTO> testCaseDataDrivenResultDTOS =
      testCaseDataDrivenResultMapper.mapDTO(testCaseDataDrivenResults.getContent());
    return new PageImpl<>(testCaseDataDrivenResultDTOS, pageable, testCaseDataDrivenResults.getTotalElements());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
  public TestCaseDataDrivenResultDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Request /test_case_results/" + id);
    TestCaseDataDrivenResult testCaseDataDriveResult = testCaseDataDrivenResultService.find(id);
    return testCaseDataDrivenResultMapper.mapDTO(testCaseDataDriveResult);
  }
}
