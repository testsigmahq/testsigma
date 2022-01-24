/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestCasePriorityDTO;
import com.testsigma.dto.export.TestCasePriorityXMLDTO;
import com.testsigma.model.TestCasePriority;
import com.testsigma.web.request.TestCasePriorityRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestCasePriorityMapper {
  List<TestCasePriorityXMLDTO> mapTestCasePriorities(List<TestCasePriority> applications);

  TestCasePriorityDTO map(TestCasePriority testCasePriority);

  List<TestCasePriorityDTO> map(List<TestCasePriority> testCasePriorities);

  TestCasePriority map(TestCasePriorityRequest testCasePriorityRequest);

  void merge(TestCasePriorityRequest testCasePriorityRequest, @MappingTarget TestCasePriority testCasePriority);
}
