/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestSuiteDTO;
import com.testsigma.dto.TestSuiteEntityDTO;
import com.testsigma.dto.export.TestSuiteCloudXMLDTO;
import com.testsigma.dto.export.TestSuiteXMLDTO;
import com.testsigma.model.TestSuite;
import com.testsigma.web.request.TestSuiteRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestSuiteMapper {
  List<TestSuiteXMLDTO> mapTestSuites(List<TestSuite> testSuites);

  TestSuiteEntityDTO map(TestSuite suite);

  @Mapping(target = "lastRun.testSuite", ignore = true)
  @Mapping(target = "lastRun.testDeviceResult", ignore = true)
  @Mapping(target = "preRequisiteSuite.lastRun", ignore = true)
  TestSuiteDTO mapToDTO(TestSuite suite);

  List<TestSuiteDTO> mapToDTO(List<TestSuite> suite);

  TestSuite map(TestSuiteRequest testSuiteRequest);

  @Mapping(target = "preRequisite", expression = "java(request.getPreRequisite())")
  void merge(TestSuiteRequest request, @MappingTarget TestSuite testSuite);

    TestSuite copy(TestSuite testSuite);

  List<TestSuite> mapCloudTestSuiteList(List<TestSuiteCloudXMLDTO> readValue);

  List<TestSuite> mapTestSuiteList(List<TestSuiteXMLDTO> readValue);
}

