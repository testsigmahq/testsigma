/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.ElementFilterDTO;
import com.testsigma.model.ElementFilter;
import com.testsigma.web.request.ElementFilterRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ElementFilterMapper {
  ElementFilter map(ElementFilterRequest request);

  void merge(@MappingTarget ElementFilter elementFilter, ElementFilterRequest request);

  List<ElementFilterDTO> map(List<ElementFilter> elementFilters);

  ElementFilterDTO map(ElementFilter elementFilter);

}
