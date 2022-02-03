/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.ScheduleTestPlanDTO;
import com.testsigma.model.ScheduleTestPlan;
import com.testsigma.web.request.ScheduleTestPlanRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ScheduleTestPlanMapper {

  ScheduleTestPlan map(ScheduleTestPlanRequest request);

  @Mapping(target = "scheduleTime", expression = "java(new java.text.SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ss'Z'\").format(scheduleTestPlan.getScheduleTime()))")
  ScheduleTestPlanDTO mapToDTO(ScheduleTestPlan scheduleTestPlan);

  List<ScheduleTestPlanDTO> mapToDTO(List<ScheduleTestPlan> attachments);

  void merge(ScheduleTestPlanRequest request, @MappingTarget ScheduleTestPlan scheduleTestPlan);
}
