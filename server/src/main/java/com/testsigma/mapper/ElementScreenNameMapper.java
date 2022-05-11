/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.mapper;

import com.testsigma.dto.ElementScreenNameDTO;
import com.testsigma.dto.export.ElementScreenNameCloudXMLDTO;
import com.testsigma.dto.export.ElementScreenNameXMLDTO;
import com.testsigma.model.ElementScreenName;
import com.testsigma.web.request.ElementScreenNameRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ElementScreenNameMapper {

  List<ElementScreenNameXMLDTO> mapElementScreenNameList(List<ElementScreenName> elementScreenNames);

  @IterableMapping(qualifiedByName = "mapData")
  List<ElementScreenNameDTO> map(List<ElementScreenName> screenNames);

  ElementScreenName map(ElementScreenNameRequest screenNameRequest);

  @Named("mapData")
  ElementScreenNameDTO map(ElementScreenName screenName);

    List<ElementScreenName> mapElementScreenNamesList(List<ElementScreenNameXMLDTO> readValue);
    List<ElementScreenName> mapCloudElementScreenNamesList(List<ElementScreenNameCloudXMLDTO> readValue);

  ElementScreenName copy(ElementScreenName uiIdentifier);
}
