/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestCaseTypeDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestCaseTypeMapper;
import com.testsigma.model.TestCaseType;
import com.testsigma.service.TestCaseTypeService;
import com.testsigma.specification.TestCaseTypeSpecificationsBuilder;
import com.testsigma.web.request.TestCaseTypeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test_case_types")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestCaseTypesController {

  private final TestCaseTypeService testCaseTypeService;
  private final TestCaseTypeMapper testCaseTypeMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestCaseTypeDTO> index(TestCaseTypeSpecificationsBuilder builder, Pageable pageable) {
    Specification<TestCaseType> spec = builder.build();
    Page<TestCaseType> uploads = testCaseTypeService.findAll(spec, pageable);
    List<TestCaseTypeDTO> uploadDTOS = testCaseTypeMapper.map(uploads.getContent());
    return new PageImpl<>(uploadDTOS, pageable, uploads.getTotalElements());
  }

  @GetMapping("/{id}")
  public TestCaseTypeDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    TestCaseType testCaseType = this.testCaseTypeService.find(id);
    return testCaseTypeMapper.map(testCaseType);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public TestCaseTypeDTO update(@PathVariable("id") Long id, @RequestBody TestCaseTypeRequest request) throws ResourceNotFoundException {
    TestCaseType testCaseType = this.testCaseTypeService.find(id);
    this.testCaseTypeMapper.merge(request, testCaseType);
    testCaseType = this.testCaseTypeService.update(testCaseType);
    return testCaseTypeMapper.map(testCaseType);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestCaseTypeDTO create(@RequestBody TestCaseTypeRequest request) throws ResourceNotFoundException {
    TestCaseType testCaseType = this.testCaseTypeMapper.map(request);
    testCaseType = this.testCaseTypeService.create(testCaseType);
    return testCaseTypeMapper.map(testCaseType);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    this.testCaseTypeService.destroy(id);
  }
}
