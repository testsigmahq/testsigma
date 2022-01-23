/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestSuiteDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestSuiteMapper;
import com.testsigma.model.TestSuite;
import com.testsigma.service.TestSuiteService;
import com.testsigma.specification.TestSuiteSpecificationsBuilder;
import com.testsigma.web.request.TestSuiteRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = "/test_suites")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestSuitesController {

  private final TestSuiteService testSuiteService;
  private final TestSuiteMapper testSuiteMapper;


  @RequestMapping(method = RequestMethod.GET)
  public Page<TestSuiteDTO> index(TestSuiteSpecificationsBuilder builder,
                                  @PageableDefault(value = 25, page = 0) Pageable pageable) {
    Specification<TestSuite> spec = builder.build();
    Page<TestSuite> testSuites = testSuiteService.findAll(spec, pageable);
    List<TestSuiteDTO> testSuiteDTOS = testSuiteMapper.mapToDTO(testSuites.getContent());
    return new PageImpl<>(testSuiteDTOS, pageable, testSuites.getTotalElements());
  }

  @GetMapping(path = "/{id}")
  public TestSuiteDTO show(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    TestSuite testSuite = this.testSuiteService.find(id);
    return testSuiteMapper.mapToDTO(testSuite);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestSuiteDTO create(@RequestBody TestSuiteRequest request) throws TestsigmaException {
    TestSuite testSuite = this.testSuiteMapper.map(request);
    testSuite.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testSuite = this.testSuiteService.create(testSuite);
    return testSuiteMapper.mapToDTO(testSuite);
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public TestSuiteDTO update(@PathVariable(value = "id") Long id, @RequestBody TestSuiteRequest request) throws TestsigmaException {
    TestSuite testSuite = this.testSuiteService.find(id);
    testSuiteMapper.merge(request, testSuite);
    testSuite.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testSuite = this.testSuiteService.update(testSuite);
    return testSuiteMapper.mapToDTO(testSuite);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    this.testSuiteService.destroy(id);
  }

  @DeleteMapping(value = "/bulk")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids) throws Exception {
    this.testSuiteService.bulkDelete(ids);
  }
}
