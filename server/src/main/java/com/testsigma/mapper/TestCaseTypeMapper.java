/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestCaseTypeDTO;
import com.testsigma.dto.export.TestCasePriorityCloudXMLDTO;
import com.testsigma.dto.export.TestCaseTypeCloudXMLDTO;
import com.testsigma.dto.export.TestCaseTypeXMLDTO;
import com.testsigma.model.TestCaseType;
import com.testsigma.web.request.TestCaseTypeRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCaseTypeMapper {
  List<TestCaseTypeXMLDTO> mapTestCaseTypes(List<TestCaseType> applications);

  TestCaseTypeDTO map(TestCaseType testCaseType);

  List<TestCaseTypeDTO> map(List<TestCaseType> testCaseTypes);

  TestCaseType map(TestCaseTypeRequest testCaseTypeRequest);

  void merge(TestCaseTypeRequest testCaseTypeRequest, @MappingTarget TestCaseType testCaseType);

  List<TestCaseType> mapTestCaseTypeList(List<TestCaseTypeXMLDTO> readValue);


  TestCaseType copy(TestCaseType testCasePriority);

  List<TestCaseType> mapTestCaseTypeCloudList(List<TestCaseTypeCloudXMLDTO> readValue);
}
