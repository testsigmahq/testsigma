/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.api.APITestPlanResultDTO;
import com.testsigma.dto.TestPlanResultDTO;
import com.testsigma.model.TestPlanResult;
import com.testsigma.web.request.TestPlanResultRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)

public interface TestPlanResultMapper {
  @Mapping(target = "testPlan.lastRun", ignore = true)
  TestPlanResultDTO mapTo(TestPlanResult testPlanResult);

  @Mapping(target = "testPlan.lastRun", ignore = true)
  @Mapping(target = "dryTestPlanId",expression = "java(testPlanResult.getDryTestPlan() != null ? testPlanResult.getDryTestPlan().getId() : null)")
  @Mapping(target = "testDeviceId",expression = "java(testPlanResult.getEnvironment() != null ?testPlanResult.getEnvironment().getId() : null)")
  APITestPlanResultDTO mapToApi(TestPlanResult testPlanResult);

  @Mapping(target = "testPlanId", ignore = true)
  @Mapping(target = "reRunType", ignore = true)
  void merge(TestPlanResultRequest testPlanResultRequest, @MappingTarget TestPlanResult testPlanResult);

  List<TestPlanResultDTO> map(List<TestPlanResult> results);
  List<APITestPlanResultDTO> mapApi(List<TestPlanResult> results);
}
