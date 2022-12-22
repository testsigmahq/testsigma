/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.api.APITestDeviceResultDTO;
import com.testsigma.dto.api.APITestDeviceSettingsDTO;
import com.testsigma.dto.api.APITestPlanResultDTO;
import com.testsigma.dto.*;
import com.testsigma.model.*;
import com.testsigma.web.request.EnvironmentRunResultRequest;
import com.testsigma.web.request.TestDeviceResultRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestDeviceResultMapper {

  @Mapping(target = "duration", expression = "java(environmentRunResult.getEndTime().getTime() - environmentRunResult" +
    ".getStartTime().getTime())")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "testDeviceId", ignore = true)
  @Mapping(target = "status", ignore = true)
  void merge(EnvironmentRunResultRequest environmentRunResult, @MappingTarget TestDeviceResult testDeviceResult);

  void mergeRequest(TestDeviceResultRequest testDeviceResultRequest, @MappingTarget TestDeviceResult testDeviceResult);

  @Mapping(target = "environmentResultId", source = "id")
  @Mapping(target = "testPlanId", expression = "java(testDeviceResult.getTestPlanResult().getTestPlan().getId())")
  @Mapping(target = "name", expression = "java(testDeviceResult.getTestPlanResult().getTestPlan().getName())")
  @Mapping(target = "executionRunId", source = "testPlanResultId")
  EnvironmentEntityDTO map(TestDeviceResult testDeviceResult);


  TestDeviceDTO map(TestDevice results);

  @Mapping(target = "lastRun.testPlan", ignore = true)
  TestPlanDTO map(AbstractTestPlan execution);

  TestPlanResultDTO map(TestPlanResult testPlanResult);

  List<TestDeviceResultDTO> mapDTO(List<TestDeviceResult> results);
  List<APITestDeviceResultDTO> mapApiDTO(List<TestDeviceResult> results);

  TestDeviceResultDTO mapDTO(TestDeviceResult result);

  @Mapping(target = "result",expression = "java(result.getResult())")
  @Mapping(target = "testPlanResultId",expression = "java(result.getTestPlanResult() != null ?result.getTestPlanResult().getId():null)")
  @Mapping(target = "testDeviceId",expression = "java(result.getTestDevice()!=null?result.getTestDevice().getId():null)")
  APITestDeviceResultDTO mapApiDTO(TestDeviceResult result);

}
