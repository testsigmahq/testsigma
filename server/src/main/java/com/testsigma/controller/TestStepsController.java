/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.RestStepResponseDTO;
import com.testsigma.dto.TestStepDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestStepMapper;
import com.testsigma.model.TestStep;
import com.testsigma.model.TestStepPriority;
import com.testsigma.service.TestStepService;
import com.testsigma.specification.TestStepSpecificationsBuilder;
import com.testsigma.util.HttpClient;
import com.testsigma.web.request.RestStepRequest;
import com.testsigma.web.request.TestStepRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@RestController
@RequestMapping(path = "/test_steps", produces = MediaType.APPLICATION_JSON_VALUE)
@Log4j2
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestStepsController {

  private final HttpClient httpClient;
  private final TestStepService service;
  private final TestStepMapper mapper;

  @RequestMapping(path = "/fetch_rest_response", method = RequestMethod.POST)
  public RestStepResponseDTO fetchApiResponse(@RequestBody RestStepRequest restStepRequest) {
    log.debug("GET /test_steps/fetch_rest_response with request" + restStepRequest);
    return this.httpClient.execute(restStepRequest);
  }

  @RequestMapping(method = RequestMethod.GET)
  public Page<TestStepDTO> index(TestStepSpecificationsBuilder builder, @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {
    log.debug("GET /test_steps ");
    Specification<TestStep> spec = builder.build();
    Page<TestStep> testStep = this.service.findAll(spec, pageable);
    List<TestStepDTO> testDataDTOS =
      mapper.mapDTOs(testStep.getContent());
    return new PageImpl<>(testDataDTOS, pageable, testStep.getTotalElements());
  }

  @DeleteMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
    log.debug("DELETE /test_steps with id::" + id);
    TestStep testStep = this.service.find(id);
    service.destroy(testStep);
  }

  @PutMapping(path = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public TestStepDTO update(@PathVariable(value = "id") Long id, @RequestBody TestStepRequest request) throws TestsigmaException {
    log.debug("PUT /test_steps with id::" + id + " request::" + request);
    TestStep testStep = this.service.find(id);
    mapper.merge(request, testStep);
    testStep.setUpdatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    testStep = this.service.update(testStep);
    return this.mapper.mapDTO(testStep);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestStepDTO create(@RequestBody TestStepRequest request) throws TestsigmaException {
    log.debug("POST /test_steps with request::" + request);
    TestStep testStep = mapper.map(request);
    testStep.setCreatedDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    if (testStep.getParentId() != null)
      testStep.setDisabled(this.service.find(testStep.getParentId()).getDisabled());
    testStep = service.create(testStep);
    return mapper.mapDTO(testStep);
  }

  @DeleteMapping(value = "/bulk_delete")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkDelete(@RequestParam(value = "ids[]") Long[] ids) throws ResourceNotFoundException {
    // [TODO] [Pratheepv] position update is stopping from bulk delete query
    log.debug("DELETE /test_steps/bulk_update_properties with ids::" + Arrays.toString(ids));
    for (Long id : ids) {
      TestStep step = this.service.find(id);
      this.service.destroy(step);
    }
  }

  @PutMapping(value = "/bulk_update_properties")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkUpdateProperties(@RequestParam(value = "ids[]") Long[] ids,
                                   @RequestParam(value = "waitTime", required = false) Integer waitTime,
                                   @RequestParam(value = "priority", required = false) TestStepPriority testStepPriority,
                                   @RequestParam(value = "disabled", required = false) Boolean disabled,
                                   @RequestParam(value = "ignoreStepResult", required = false) Boolean ignoreStepResult,
                                   @RequestParam(value = "visualEnabled", required = false) Boolean visualEnabled) {

    log.debug("PUT /test_steps/bulk_update_properties with ids::" + Arrays.toString(ids) + " waitTime ::"
      + waitTime + " priority ::" + testStepPriority + " disabled ::" + disabled +" ignoreStepResult ::" +ignoreStepResult);
    this.service.bulkUpdateProperties(ids, testStepPriority, waitTime, disabled, ignoreStepResult,visualEnabled);
  }

  @PutMapping(value = "/bulk_update")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void bulkUpdate(@RequestBody List<TestStepRequest> testStepRequests) {
    log.debug("PUT /test_steps/bulk_update with body::" + testStepRequests);
    for (TestStepRequest request : testStepRequests) {
      TestStep step = this.mapper.map(request);
      this.service.update(step);
    }
  }
}
