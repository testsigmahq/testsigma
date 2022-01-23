/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.mapper;

import com.testsigma.dto.IntegrationsDTO;
import com.testsigma.model.Integrations;
import com.testsigma.web.request.IntegrationsRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface IntegrationsMapper {
  IntegrationsDTO map(Integrations entity);

  @Mapping(target = "workspace", expression = "java(com.testsigma.model.Integration.getIntegration(request.getWorkspaceId()))")
  Integrations map(IntegrationsRequest request);

  List<IntegrationsDTO> map(List<Integrations> configs);
}
