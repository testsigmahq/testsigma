/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import com.testsigma.dto.*;
import com.testsigma.dto.export.TestStepXMLDTO;
import com.testsigma.model.*;
import com.testsigma.web.request.TestStepRequest;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestStepMapper {
  List<TestStepXMLDTO> mapTestSteps(List<TestStep> testSteps);
  List<TestStepDTO> mapTestStepsToDTO(List<TestStep> testSteps);
  TestStep map(TestStepDTO testStepDTO);


  @Mapping(target = "addonTestData", expression = "java(testStep.getAddonTestData())")
  @Mapping(target = "addonElements", expression = "java(testStep.getAddonElements())")
  TestCaseStepEntityDTO mapEntity(TestStep testStep);

//  @Mapping(target = "testDataFunction", expression = "java(this.mapTestDataFunction(testStepDataMap.getTestDataFunction()))")
  @Mapping(target = "forLoop", expression = "java(this.mapForLoop(testStepDataMap.getForLoop()))")
  @Mapping(target = "testDataMap", expression = "java(this.mapTestData(testStepDataMap.getTestData()))")
  TestStepDataMapEntityDTO mapDataMap(TestStepDataMap testStepDataMap);

  TestStepForLoopEntityDTO mapForLoop(TestStepForLoop testStepForLoop);

  Map<String, TestStepNlpDataEntityDTO> mapTestData(Map<String, TestStepNlpData> testDataMap);

  DefaultDataGeneratorsEntity mapTestDataFunction(DefaultDataGeneratorsDetails defaultDataGeneratorsDetails);


  TestStepDTO mapDTO(TestStep testStep);
  @Mapping(target = "status", source = "expectedResponseStatus")
  RestStepDTO map(RestStep restStep);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "addonTestData", expression = "java(testStep.getAddonTestData())")
  @Mapping(target = "addonElements", expression = "java(testStep.getAddonElements())")
  @Mapping(target = "addonTDF", expression = "java(testStep.getAddonTDF())")
  @Mapping(target = "entityExternalMapping", ignore = true)
  TestStep copy(TestStep testStep);

  List<TestStepDTO> mapDTOs(List<TestStep> testSteps);

  @Mapping(target = "preRequisiteStepId", expression = "java(testStepRequest.getPreRequisiteStepId())")
  @Mapping(target = "testDataProfileStepId", expression = "java(testStepRequest.getTestDataProfileStepId())")
  @Mapping(target = "addonTestData", expression = "java(testStepRequest.getAddonTestData())")
  @Mapping(target = "addonElements", expression = "java(testStepRequest.getAddonElements())")
  TestStep map(TestStepRequest testStepRequest);

  @Mapping(target = "addonTestData", expression = "java(request.getAddonTestData())")
  @Mapping(target = "addonElements", expression = "java(request.getAddonElements())")
  @Mapping(target = "preRequisiteStepId", expression = "java(request.getPreRequisiteStepId())")
  @Mapping(target = "naturalTextActionId", expression = "java(request.getNaturalTextActionId())")
  @Mapping(target = "addonActionId", expression = "java(request.getAddonActionId())")
  @Mapping(target = "dataMap", expression = "java(mapFromDataMap(request.getDataMap()))")
  @Mapping(target = "element", expression = "java(request.getDataMap().getElement())")
  @Mapping(target = "attribute", expression = "java(request.getDataMap().getAttribute())")
  @Mapping(target = "forLoopStartIndex", expression = "java(request.getDataMap().getForLoop().getStartIndex())")
  @Mapping(target = "forLoopEndIndex", expression = "java(request.getDataMap().getForLoop().getEndIndex())")
  @Mapping(target = "forLoopTestDataId", expression = "java(request.getDataMap().getForLoop().getTestDataId())")
  @Mapping(target = "maxIterations", expression = "java(request.getMaxIterations())")
  void merge(TestStepRequest request, @MappingTarget TestStep testStep);

  @Mapping(target = "testStep", ignore = true)
  RestStep mapRest(RestStep restEntity);

  @Mapping(target = "method", expression = "java(restStepDTO.getMethod().name())")
  @Mapping(target = "requestHeaders", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getRequestHeaders(), \"\").toString())")
  @Mapping(target = "responseHeaders", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getResponseHeaders(), \"\").toString())")
  @Mapping(target = "responseCompareType", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getResponseCompareType(), com.testsigma.model.RestStepCompareType.LENIENT).name())")
  @Mapping(target = "headerCompareType", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getHeaderCompareType(), com.testsigma.model.RestStepCompareType.LENIENT).name())")
  @Mapping(target = "headerRuntimeData", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getHeaderRuntimeData(), \"\").toString())")
  @Mapping(target = "bodyRuntimeData", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getBodyRuntimeData(), \"\").toString())")
  @Mapping(target = "authorizationType", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getAuthorizationType(), com.testsigma.model.RestStepAuthorizationType.NONE).ordinal())")
  @Mapping(target = "authorizationValue", expression = "java(org.apache.commons.lang3.ObjectUtils.defaultIfNull(restStepDTO.getAuthorizationValue(), \"\").toString())")
  RestStepEntityDTO mapStepEntity(RestStepDTO restStepDTO);

  default TestStepData mapFromDataMap(TestStepDataMap testStepDataMap) {
    TestStepData testStepData = new TestStepData();
    if(testStepDataMap != null && testStepDataMap.getTestData() != null) {
      testStepData.setTestData(testStepDataMap.getTestData());
    }
    return testStepData;
  }
}

