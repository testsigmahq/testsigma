/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.NaturalTextActionsDTO;
import com.testsigma.dto.NaturaltextActionExampleDTO;
import com.testsigma.model.NaturalTextActionExample;
import com.testsigma.model.NaturalTextActions;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NaturalTextActionMapper {
  List<NaturalTextActionsDTO> mapDTO(List<NaturalTextActions> naturalTextActions);

  NaturalTextActionsDTO mapDTO(NaturalTextActions naturalTextActions);

  NaturaltextActionExampleDTO map(NaturalTextActionExample example);
}
