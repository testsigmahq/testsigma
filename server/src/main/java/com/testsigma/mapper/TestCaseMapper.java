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
import com.testsigma.dto.TestDataProfileDTO;
import com.testsigma.dto.TestDataSetDTO;
import com.testsigma.dto.export.ElementCloudXMLDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.dto.export.TestCaseCloudXMLDTO;
import com.testsigma.dto.export.TestCaseDataDrivenConditionCloudXMLDTO;
import com.testsigma.dto.export.TestCaseXMLDTO;
import com.testsigma.model.DataDrivenIterationType;
import com.testsigma.model.Operator;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.web.request.TestCaseRequest;
import com.testsigma.web.request.testproject.TestProjectTestCaseRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCaseMapper {
  List<TestCaseXMLDTO> mapTestcases(List<TestCase> requirements);
  List<TestCaseCloudXMLDTO> mapToCloudTestcases(List<TestCase> testCases);
  
  @Mapping(target = "testCaseDataDrivenCondition", expression = "java(mapToCloudDataDrivenCondition(testCase))")
  TestCaseCloudXMLDTO mapToCloudTestcase(TestCase testCase);
  
  default TestCaseDataDrivenConditionCloudXMLDTO mapToCloudDataDrivenCondition(TestCase testCase) {
    if (testCase == null || testCase.getTestDataId() == null) return null;
    TestCaseDataDrivenConditionCloudXMLDTO condition =  new TestCaseDataDrivenConditionCloudXMLDTO();
    condition.setTestCaseId(testCase.getId());
    condition.setTestDataProfileId(testCase.getTestDataId());
    condition.setTestDataIndex(Long.valueOf(testCase.getTestDataStartIndex()));
    Long endIndex = testCase.getTestDataEndIndex() != null ? Long.valueOf(testCase.getTestDataEndIndex()) : -1L;
    condition.setTestDataEndIndex(endIndex);
    if (testCase.getIsDataDriven()) {
      condition.setIterationType(DataDrivenIterationType.INDEX);
      condition.setOperator(Operator.BETWEEN);
    }
    return condition;
  }

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
  @Mapping(target = "testData", expression = "java(testDataToTestDataProfileDTO(testCase.getTestData()))")
  TestCaseDTO mapDTO(TestCase testCase);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "results", ignore = true)
  @Mapping(target = "tagUses", ignore = true)
  @Mapping(target = "testSteps", ignore = true)
  @Mapping(target = "suiteTestCaseMappings", ignore = true)
  TestCase copy(TestCase testCase);

  TestCase map(TestProjectTestCaseRequest testCaseRequest);

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

  @Mapping(target = "testDataStartIndex", source = "testDataStartIndex", defaultValue = "0")
  TestCase testCaseCloudXMLDTOToTestCase(TestCaseCloudXMLDTO testCaseCloudXMLDTO);

  List<TestDataSetDTO> testDataSetListToTestDataSetDTOList(List<TestDataSet> dataSets);

  default TestDataProfileDTO testDataToTestDataProfileDTO(TestData testData) {
    if ( testData == null ) {
      return null;
    }

    TestDataProfileDTO testDataProfileDTO = new TestDataProfileDTO();

    if ( testData.getId() != null ) {
      testDataProfileDTO.setId( testData.getId() );
    }
    if ( testData.getTestDataName() != null ) {
      testDataProfileDTO.setTestDataName( testData.getTestDataName() );
    }
    List<TestDataSetDTO> list = testDataSetListToTestDataSetDTOList( testData.getTempTestData() );
    if ( list != null ) {
      testDataProfileDTO.setData( list );
    }
    if ( testData.getCreatedDate() != null ) {
      testDataProfileDTO.setCreatedDate( testData.getCreatedDate() );
    }
    if ( testData.getUpdatedDate() != null ) {
      testDataProfileDTO.setUpdatedDate( testData.getUpdatedDate() );
    }
    if ( testData.getVersionId() != null ) {
      testDataProfileDTO.setVersionId( testData.getVersionId() );
    }
    if ( testData.getIsMigrated() != null ) {
      testDataProfileDTO.setIsMigrated( testData.getIsMigrated() );
    }

    testDataProfileDTO.setTestData( null );

    return testDataProfileDTO;
  }


}

