/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.SuggestionResultMappingDTO;
import com.testsigma.model.SuggestionResultMapping;
import com.testsigma.web.request.SuggestionEngineResultRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SuggestionResultMappingMapper {
  @Mapping(target = "metaData", expression = "java(request.getMetaData())")
  SuggestionResultMapping map(SuggestionEngineResultRequest request);

  List<SuggestionResultMappingDTO> map(List<SuggestionResultMapping> suggestionResultMappings);

  SuggestionResultMappingDTO map(SuggestionResultMapping result);
}
