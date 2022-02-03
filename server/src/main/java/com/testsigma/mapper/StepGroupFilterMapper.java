/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.StepGroupFilterDTO;
import com.testsigma.model.StepGroupFilter;
import com.testsigma.web.request.StepGroupFilterRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StepGroupFilterMapper {
  StepGroupFilter map(StepGroupFilterRequest request);

  void merge(@MappingTarget StepGroupFilter filter, StepGroupFilterRequest request);

  List<StepGroupFilterDTO> map(List<StepGroupFilter> filters);

  StepGroupFilterDTO map(StepGroupFilter filter);

}
