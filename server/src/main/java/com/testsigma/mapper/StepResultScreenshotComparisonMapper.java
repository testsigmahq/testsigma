/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.ElementMetaDataDTO;
import com.testsigma.dto.StepResultScreenshotComparisonDTO;
import com.testsigma.model.ElementMetaData;
import com.testsigma.model.StepResultScreenshotComparison;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StepResultScreenshotComparisonMapper {

  ElementMetaDataDTO mapMetaData(ElementMetaData elementMetaData);

  StepResultScreenshotComparisonDTO map(StepResultScreenshotComparison screenshotComparison);

  List<StepResultScreenshotComparisonDTO> map(List<StepResultScreenshotComparison> screenshotComparisons);
}
