/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.OpensourceDTO;
import com.testsigma.model.TestsigmaOSConfig;
import com.testsigma.web.request.OpensourceRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestsigmaOSConfigMapper {

  OpensourceDTO map(TestsigmaOSConfig testsigmaOSConfig);

  void merge(OpensourceRequest request, @MappingTarget TestsigmaOSConfig testsigmaOSConfig);

}
