/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestStepScreenshotDTO;
import com.testsigma.model.TestStepScreenshot;
import com.testsigma.web.request.TestStepScreenshotRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestStepScreenshotMapper {

  void merge(TestStepScreenshotRequest request, @MappingTarget TestStepScreenshot stepScreenshot);

  TestStepScreenshotDTO map(TestStepScreenshot testStepScreenshot);

}
