/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.mapper;

import com.testsigma.dto.UserPreferenceDTO;
import com.testsigma.model.UserPreference;
import com.testsigma.web.request.UserPreferenceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserPreferenceMapper {
  UserPreferenceDTO map(UserPreference userPreference);

  void merge(UserPreferenceRequest userPreferenceRequest
    , @MappingTarget UserPreference userPreference);
}
