/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.TestCaseFilterDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestCaseFilterMapper;
import com.testsigma.model.TestCaseFilter;
import com.testsigma.service.TestCaseFilterService;
import com.testsigma.web.request.TestCaseFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/test_case_filters")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestCaseFiltersController {

  private final TestCaseFilterService service;
  private final TestCaseFilterMapper mapper;

  @GetMapping(path = "/{id}")
  public TestCaseFilterDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Get Request /test_case_filters/" + id);
    TestCaseFilter filter = service.find(id);
    return mapper.map(filter);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public TestCaseFilterDTO update(@PathVariable("id") Long id, @RequestBody TestCaseFilterRequest request) throws ResourceNotFoundException {
    log.info("Put Request /test_case_filters/" + id + " data:" + request);
    TestCaseFilter filter = service.find(id);
    mapper.merge(filter, request);
    filter = service.update(filter);
    return mapper.map(filter);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestCaseFilterDTO create(@RequestBody TestCaseFilterRequest request) throws ResourceNotFoundException {
    log.info("Post Request /test_case_filters/ data:" + request);
    TestCaseFilter filter = mapper.map(request);
    filter = service.create(filter);
    return mapper.map(filter);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Delete Request /test_case_filters/" + id);
    service.destroy(id);
  }

  @GetMapping
  public Page<TestCaseFilterDTO> index(@RequestParam(value = "versionId") Long versionId, @PageableDefault(size = 50) Pageable pageable) {
    log.info("Get Request /test_case_filters/ versionId:" + versionId);
    Page<TestCaseFilter> elements = service.findAllVisible(versionId, pageable);
    List<TestCaseFilterDTO> elementDTOS = mapper.map(elements.getContent());
    return new PageImpl<>(elementDTOS, pageable, elements.getTotalElements());
  }
}
