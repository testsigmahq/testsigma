/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.service;

import com.testsigma.exception.ResourceNotFoundException;
import com.testsigma.model.ScheduleTestPlan;
import com.testsigma.repository.ScheduleTestPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScheduleTestPlanService {

  private final ScheduleTestPlanRepository scheduleTestPlanRepository;

  public ScheduleTestPlan find(Long id) throws ResourceNotFoundException {
    return scheduleTestPlanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("ScheduleExecution missing with" + id));
  }

  public ScheduleTestPlan create(ScheduleTestPlan scheduleTestPlan) {
    return this.scheduleTestPlanRepository.save(scheduleTestPlan);
  }

  public ScheduleTestPlan update(ScheduleTestPlan scheduleTestPlan) {
    return this.scheduleTestPlanRepository.save(scheduleTestPlan);
  }

  public void destroy(Long id) throws ResourceNotFoundException {
    ScheduleTestPlan scheduleTestPlan = this.find(id);
    this.scheduleTestPlanRepository.delete(scheduleTestPlan);
  }

  public Page<ScheduleTestPlan> findAll(Specification<ScheduleTestPlan> spec, Pageable pageable) {
    return this.scheduleTestPlanRepository.findAll(spec, pageable);
  }

  public List<ScheduleTestPlan> findAllActiveSchedules(Timestamp timestamp) {
    return this.scheduleTestPlanRepository.findAllActiveScheduleTestPlans(timestamp);
  }

}

