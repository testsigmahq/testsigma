/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestCaseDTO;
import com.testsigma.dto.TestCaseEntityDTO;
import com.testsigma.dto.export.ElementCloudXMLDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.dto.export.TestCaseCloudXMLDTO;
import com.testsigma.dto.export.TestCaseXMLDTO;
import com.testsigma.model.TestCase;
import com.testsigma.web.request.TestCaseRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCaseMapper {
  List<TestCaseXMLDTO> mapTestcases(List<TestCase> requirements);

  @Named("mapData")
  @Mapping(target = "testCaseName", source = "name")
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "testSteps", ignore = true)
  @Mapping(target = "isDataDriven", expression = "java(testCase.getIsDataDriven()!=null && testCase.getIsDataDriven())")
  @Mapping(target = "isStepGroup", expression = "java(testCase.getIsStepGroup()!=null && testCase.getIsStepGroup())")
  @Mapping(target = "startTime", expression = "java(testCase.getCalendarTimeFromTimestamp(testCase.getStartTime()))")
  @Mapping(target = "endTime", expression = "java(testCase.getCalendarTimeFromTimestamp(testCase.getEndTime()))")
  TestCaseEntityDTO map(TestCase testCase);

  @IterableMapping(qualifiedByName = "mapData")
  List<TestCaseEntityDTO> map(List<TestCase> testCases);

  @Named("mapWithoutData")
  @Mapping(target = "results", ignore = true)
  @Mapping(target = "lastRun.testCase", ignore = true)
  @Mapping(target = "lastRun.testDeviceResult", ignore = true)
  @Mapping(target = "lastRun.parentResult", ignore = true)
  @Mapping(target = "lastRun.testSuite", ignore = true)
  @Mapping(target = "testData.testData", ignore = true)
  TestCaseDTO mapDTO(TestCase testCase);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "results", ignore = true)
  @Mapping(target = "tagUses", ignore = true)
  @Mapping(target = "testSteps", ignore = true)
  @Mapping(target = "suiteTestCaseMappings", ignore = true)
  TestCase copy(TestCase testCase);


  @Mapping(target = "isDataDriven", expression = "java(testcase.getIsDataDriven()!=null && testcase.getIsDataDriven())")
  @Mapping(target = "isStepGroup", expression = "java(testcase.getIsStepGroup()!=null && testcase.getIsStepGroup())")
  @Mapping(target = "testDataEndIndex", expression = "java(testcase.getTestDataEndIndex())")
  @Mapping(target = "testDataStartIndex", expression = "java(testcase.getTestDataStartIndex())")
  TestCase map(TestCaseRequest testcase);

  @Mapping(target = "preRequisite", expression = "java( testCaseRequest.getPreRequisite())")
  @Mapping(target = "testDataId", expression = "java( testCaseRequest.getTestDataId())")
  @Mapping(target = "testDataEndIndex", expression = "java(testCaseRequest.getTestDataEndIndex())")
  @Mapping(target = "testDataStartIndex", expression = "java(testCaseRequest.getTestDataStartIndex())")
  void map(TestCaseRequest testCaseRequest, @MappingTarget TestCase testcase);

  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "results", ignore = true)
  @Mapping(target = "lastRun.testCase", ignore = true)
  @Mapping(target = "lastRun.testDeviceResult", ignore = true)
  @Mapping(target = "lastRun.parentResult", ignore = true)
  @Mapping(target = "lastRun.testSuite", ignore = true)
  TestCaseDTO mapTo(TestCase testCase);

  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "results", ignore = true)
  @Mapping(target = "lastRun.testCase", ignore = true)
  @Mapping(target = "lastRun.testDeviceResult", ignore = true)
  @Mapping(target = "lastRun.parentResult", ignore = true)
  @Mapping(target = "lastRun.testSuite", ignore = true)

  @IterableMapping(qualifiedByName = "mapWithoutData")
  List<TestCaseDTO> mapDTOs(List<TestCase> testCases);

  List<TestCase> mapTestCasesXMLList(List<TestCaseXMLDTO> readValue);

  List<TestCase> mapTestCasesCloudXMLList(List<TestCaseCloudXMLDTO> readValue);
}

