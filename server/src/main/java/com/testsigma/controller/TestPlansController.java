/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.controller;

import com.testsigma.dto.TestPlanDTO;
import com.testsigma.exception.TestsigmaDatabaseException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.mapper.TestPlanMapper;
import com.testsigma.model.TestDevice;
import com.testsigma.model.TestPlan;
import com.testsigma.service.TestDeviceService;
import com.testsigma.service.TestPlanService;
import com.testsigma.specification.TestPlanSpecificationsBuilder;
import com.testsigma.web.request.TestDeviceRequest;
import com.testsigma.web.request.TestPlanRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequestMapping(path = "/test_plans")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestPlansController {
  private final TestPlanService testPlanService;
  private final TestDeviceService testDeviceService;
  private final TestPlanMapper testPlanMapper;


  @GetMapping
  public Page<TestPlanDTO> index(TestPlanSpecificationsBuilder builder, Pageable pageable) {
    log.info("Index request /test_plans" + builder);
    Specification<TestPlan> spec = builder.build();
    Page<TestPlan> testPlans = testPlanService.findAll(spec, pageable);
    List<TestPlanDTO> testPlanDTOS = testPlanMapper.mapTo(testPlans.getContent());
    return new PageImpl<>(testPlanDTOS, pageable, testPlans.getTotalElements());
  }

  @GetMapping(value = "/{id}")
  public TestPlanDTO show(@PathVariable(value = "id") Long id) throws TestsigmaDatabaseException {
    log.info("Get request /test_plans/" + id);
    TestPlan testPlan = this.testPlanService.find(id);
    return this.testPlanMapper.mapTo(testPlan);
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public TestPlanDTO update(@PathVariable(value = "id") Long id, @RequestBody TestPlanRequest request) throws TestsigmaDatabaseException {
    log.info("Put request /test_plans/" + id + "  " + request);
    TestPlan testPlan = this.testPlanService.find(id);
    testPlan.setTestDevices(testDeviceService.findByExecutionId(testPlan.getId()));
    Set<Long> existingIds = testPlan.getTestDevices().stream().map(TestDevice::getId).collect(Collectors.toSet());
    Set<Long> newIds = request.getEnvironments().stream().map(TestDeviceRequest::getId).collect(Collectors.toSet());
    existingIds.removeAll(newIds);
    testPlan.setOrphanTestDeviceIds(existingIds);
    testPlanMapper.merge(testPlan, request);
    testPlan.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
    testPlan = this.testPlanService.updateExecutionAndEnvironments(testPlan);
    return this.testPlanMapper.mapTo(testPlan);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TestPlanDTO create(@RequestBody TestPlanRequest request) throws TestsigmaException {
    log.info("Post request /test_plans/" + request);
    TestPlan testPlan = this.testPlanMapper.map(request);
    testPlan.setCreatedDate(new Timestamp(System.currentTimeMillis()));
    testPlan = this.testPlanService.create(testPlan);
    return this.testPlanMapper.mapTo(testPlan);
  }

  @DeleteMapping(value = "/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable(value = "id") Long id) throws TestsigmaDatabaseException {
    log.info("Delete request /test_plans/" + id);
    this.testPlanService.destroy(id);
  }
}
