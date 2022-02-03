/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestCaseFilterDTO;
import com.testsigma.model.TestCaseFilter;
import com.testsigma.web.request.TestCaseFilterRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCaseFilterMapper {
  TestCaseFilter map(TestCaseFilterRequest request);

  void merge(@MappingTarget TestCaseFilter testCaseFilter, TestCaseFilterRequest request);

  List<TestCaseFilterDTO> map(List<TestCaseFilter> testCaseFilters);

  TestCaseFilterDTO map(TestCaseFilter testCaseFilter);

}
