/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.ProvisioningProfileDTO;
import com.testsigma.model.ProvisioningProfile;
import com.testsigma.web.request.ProvisioningProfileRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ProvisioningProfileMapper {

  ProvisioningProfile map(ProvisioningProfileRequest system);

  ProvisioningProfileDTO map(ProvisioningProfile system);

  List<ProvisioningProfileDTO> map(List<ProvisioningProfile> systems);

  void merge(@MappingTarget ProvisioningProfile profile, ProvisioningProfileRequest request);
}
