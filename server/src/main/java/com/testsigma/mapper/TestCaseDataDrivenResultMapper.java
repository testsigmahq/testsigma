/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestCaseDataDrivenResultDTO;
import com.testsigma.dto.TestCaseResultDTO;
import com.testsigma.model.TestCaseDataDrivenResult;
import com.testsigma.model.TestCaseResult;
import com.testsigma.web.request.TestCaseResultRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCaseDataDrivenResultMapper {

  @Mapping(target = "testCaseResultId", source = "parentId")
  @Mapping(target = "iterationResultId", source = "id")
  @Mapping(target = "testDataName", source = "testDataSetName")
  TestCaseDataDrivenResult map(TestCaseResultRequest testCaseResult);

  @Mapping(target = "iterationResult.testSuite", ignore = true)
  @Mapping(target = "iterationResult.testCase", ignore = true)
  @Mapping(target = "iterationResult.parentResult.testSuite", ignore = true)
  @Mapping(target = "iterationResult.parentResult.testDeviceResult", ignore = true)
  @Mapping(target = "iterationResult.parentResult.testCase", ignore = true)
  @Mapping(target = "iterationResult.parentResult.parentResult", ignore = true)
  @Mapping(target = "iterationResult.testDeviceResult", ignore = true)
  TestCaseDataDrivenResultDTO mapDTO(TestCaseDataDrivenResult testCaseDataDrivenResult);

  List<TestCaseDataDrivenResultDTO> mapDTO(List<TestCaseDataDrivenResult> testCaseDataDrivenResult);

  @Mapping(target = "testCase", ignore = true)
  @Mapping(target = "testDeviceResult", ignore = true)
  @Mapping(target = "testSuite", ignore = true)
  @Mapping(target = "parentResult.testSuite", ignore = true)
  @Mapping(target = "parentResult.testDeviceResult", ignore = true)
  @Mapping(target = "parentResult.testCase", ignore = true)
  @Mapping(target = "parentResult.childResult", ignore = true)
  TestCaseResultDTO mapDTO(TestCaseResult result);
}
