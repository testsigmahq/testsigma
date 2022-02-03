/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.RequirementDTO;
import com.testsigma.dto.export.RequirementXMLDTO;
import com.testsigma.model.Requirement;
import com.testsigma.web.request.RequirementRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RequirementMapper {
  List<RequirementXMLDTO> mapRequirements(List<Requirement> requirements);

  Requirement map(RequirementRequest requirementRequest);

  void merge(RequirementRequest requirementRequest, @MappingTarget Requirement requirement);

  RequirementDTO map(Requirement requirement);

  RequirementXMLDTO mapExportDtos(Requirement requirement);

  List<RequirementXMLDTO> mapExportDtos(List<Requirement> requirements);

  List<RequirementDTO> map(List<Requirement> requirements);
}
