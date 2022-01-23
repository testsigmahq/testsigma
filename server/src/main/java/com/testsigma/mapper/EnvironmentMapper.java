/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.api.APIEnvironmentDTO;
import com.testsigma.dto.EnvironmentDTO;
import com.testsigma.model.Environment;
import com.testsigma.web.request.EnvironmentRequest;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface EnvironmentMapper {
  EnvironmentDTO map(Environment environment);
  APIEnvironmentDTO mapApi(Environment environment);

  List<EnvironmentDTO> map(List<Environment> environment);
  List<APIEnvironmentDTO> mapApi(List<Environment> environment);

  default void merge(Environment environment, EnvironmentRequest request) {
    if (request == null) {
      return;
    }

    if (request.getParameters() != null) {
      environment.setParameters(request.getParameters());
    }
    if (request.getPasswords() != null) {
      environment.setPasswords(request.getPasswords());
    } else {
      environment.setPasswords(new ArrayList<String>());
    }
    if (request.getId() != null) {
      environment.setId(request.getId());
    }
    if (request.getName() != null) {
      environment.setName(request.getName());
    }
    if (request.getDescription() != null) {
      environment.setDescription(request.getDescription());
    }

  }

  Environment map(EnvironmentRequest environmentRequest);
}
