/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.TestStepScreenshotDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.mapper.TestStepScreenshotMapper;
import com.testsigma.model.TestStepScreenshot;
import com.testsigma.service.TestStepScreenshotService;
import com.testsigma.web.request.TestStepScreenshotRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/test_step_screenshots", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TestStepScreenshotsController {
  private final TestStepScreenshotService service;
  private final TestStepScreenshotMapper mapper;

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void delete(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.info("Delete Request /test_step_screenshots/" + id);
    this.service.destroy(id);
  }

  @PutMapping("/{id}")
  public TestStepScreenshotDTO update(@PathVariable(value = "id") Long id, @RequestBody TestStepScreenshotRequest request) throws ResourceNotFoundException {
    log.info("Update Request /test_step_screenshots/" + id);
    TestStepScreenshot testStepScreenshot = this.service.find(id);
    mapper.merge(request, testStepScreenshot);
    testStepScreenshot = this.service.update(testStepScreenshot);
    return mapper.map(testStepScreenshot);
  }
}
