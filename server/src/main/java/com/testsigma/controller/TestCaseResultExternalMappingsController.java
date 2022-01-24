/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestCaseResultExternalMappingDTO;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestCaseResultExternalMappingMapper;
import com.testsigma.model.TestCaseResultExternalMapping;
import com.testsigma.service.TestCaseResultExternalMappingService;
import com.testsigma.web.request.TestCaseResultExternalMappingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping(path = "/external_mappings")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestCaseResultExternalMappingsController {
  private final TestCaseResultExternalMappingService externalMappingService;
  private final TestCaseResultExternalMappingMapper mapper;

  @GetMapping
  public List<TestCaseResultExternalMappingDTO> findByResultId(@NotNull @RequestParam("test_case_result_id") Long resultId) {
    List<TestCaseResultExternalMapping> list = this.externalMappingService.findByResultId(resultId);
    return mapper.mapToDTO(list);
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws TestsigmaException, IOException {
    TestCaseResultExternalMapping mapping = this.externalMappingService.find(id);
    externalMappingService.destroy(mapping);
  }

  @GetMapping(path = "/{id}")
  public TestCaseResultExternalMappingDTO show(@PathVariable("id") Long id) throws TestsigmaException, IOException {
    TestCaseResultExternalMapping mapping = this.externalMappingService.fetch(id);
    return mapper.mapToDTO(mapping);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestCaseResultExternalMappingDTO create(@RequestBody TestCaseResultExternalMappingRequest request) throws TestsigmaException, IOException, URISyntaxException {
    TestCaseResultExternalMapping mapping = mapper.map(request);
    mapping = externalMappingService.create(mapping);
    return mapper.mapToDTO(mapping);
  }
}
