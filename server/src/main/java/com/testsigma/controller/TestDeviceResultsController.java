/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.TestDeviceResultDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestDeviceResultMapper;
import com.testsigma.model.TestDeviceResult;
import com.testsigma.service.TestDeviceResultService;
import com.testsigma.specification.TestDeviceResultSpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping(path = "/test_device_results", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestDeviceResultsController {
  private final TestDeviceResultService testDeviceResultService;
  private final TestDeviceResultMapper testDeviceResultMapper;

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestDeviceResultDTO> index(TestDeviceResultSpecificationsBuilder builder, Pageable pageable) {
    log.info("Request /test_device_results list request received");
    Specification<TestDeviceResult> spec = builder.build();
    Page<TestDeviceResult> environmentResults = testDeviceResultService.findAll(spec, pageable);
    List<TestDeviceResultDTO> testDeviceResultDTOS =
      testDeviceResultMapper.mapDTO(environmentResults.getContent());
    return new PageImpl<>(testDeviceResultDTOS, pageable, environmentResults.getTotalElements());
  }

  @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
  public TestDeviceResultDTO show(@PathVariable(value = "id") Long id) throws
    ResourceNotFoundException {
    log.info("Get request /test_device_results/" + id + " received");
    TestDeviceResult testDeviceResult = testDeviceResultService.find(id);
    return testDeviceResultMapper.mapDTO(testDeviceResult);
  }
}
