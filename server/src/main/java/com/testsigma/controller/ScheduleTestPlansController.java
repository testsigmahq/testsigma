/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.controller;

import com.testsigma.dto.ScheduleTestPlanDTO;
import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.exception.TestsigmaException;
import com.testsigma.factory.SchedulerFactory;
import com.testsigma.mapper.ScheduleTestPlanMapper;
import com.testsigma.model.ScheduleTestPlan;
import com.testsigma.service.ScheduleTestPlanService;
import com.testsigma.specification.ScheduleTestPlanSpecificationsBuilder;
import com.testsigma.util.SchedulerService;
import com.testsigma.web.request.ScheduleTestPlanRequest;
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

@RestController
@RequestMapping("/schedule_test_plans")
@Log4j2
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ScheduleTestPlansController {

  private final ScheduleTestPlanService service;
  private final ScheduleTestPlanMapper mapper;
  private final SchedulerService schedulerService;
  private final SchedulerFactory schedulerFactory;

  @RequestMapping(method = RequestMethod.GET)
  public Page<ScheduleTestPlanDTO> index(Pageable pageable, ScheduleTestPlanSpecificationsBuilder builder) {
    log.info("Index request /schedule_test_plans" + builder);
    Specification<ScheduleTestPlan> spec = builder.build();
    Page<ScheduleTestPlan> scheduleTestPlans = service.findAll(spec, pageable);
    List<ScheduleTestPlanDTO> dtos = mapper.mapToDTO(scheduleTestPlans.getContent());
    return new PageImpl<>(dtos, pageable, scheduleTestPlans.getTotalElements());
  }

  @GetMapping("/{id}")
  public ScheduleTestPlanDTO show(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Show request /schedule_test_plans/" + id);
    ScheduleTestPlan scheduleTestPlan = this.service.find(id);
    return mapper.mapToDTO(scheduleTestPlan);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ScheduleTestPlanDTO create(@RequestBody ScheduleTestPlanRequest request) throws TestsigmaException {
    log.info("Create request /schedule_test_plans/" + request);
    ScheduleTestPlan scheduleTestPlan = this.mapper.map(request);
    ScheduleTestPlan entity = mapper.map(request);
    schedulerService.validateScheduleTime(entity.getScheduleTime());
    Timestamp timestamp = entity.getScheduleTime();
    scheduleTestPlan.setScheduleTime(timestamp);
    scheduleTestPlan = this.service.create(scheduleTestPlan);
    return mapper.mapToDTO(scheduleTestPlan);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ScheduleTestPlanDTO update(@PathVariable("id") Long id, @RequestBody ScheduleTestPlanRequest request) throws TestsigmaException {
    log.info("Update request /schedule_test_plans/" + id + request);
    ScheduleTestPlan scheduleTestPlan = this.service.find(id);
    this.mapper.merge(request, scheduleTestPlan);
    ScheduleTestPlan entity = mapper.map(request);
    schedulerService.validateScheduleTime(entity.getScheduleTime());
    scheduleTestPlan.setScheduleTime(entity.getScheduleTime());
    scheduleTestPlan = this.service.create(scheduleTestPlan);
    return mapper.mapToDTO(scheduleTestPlan);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public void destroy(@PathVariable("id") Long id) throws ResourceNotFoundException {
    log.info("Delete request /schedule_test_plans/" + id);
    service.destroy(id);
  }
}
