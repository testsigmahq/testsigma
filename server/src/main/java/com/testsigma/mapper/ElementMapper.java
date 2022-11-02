/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.api.APIElementDTO;
import com.testsigma.dto.ElementDTO;
import com.testsigma.dto.ElementNotificationDTO;
import com.testsigma.dto.export.ElementCloudXMLDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.model.Element;
import com.testsigma.model.ElementMetaData;
import com.testsigma.model.ElementMetaDataRequest;
import com.testsigma.web.request.ElementRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ElementMapper {

  List<ElementXMLDTO> mapElements(List<Element> elementsList);

  @Mapping(target = "currentElement", expression = "java(elementMetaDataRequest.getStringCurrentElement())")
  ElementMetaData map(ElementMetaDataRequest elementMetaDataRequest);

  ElementDTO map(Element element);
  APIElementDTO mapToApi(Element element);

  List<APIElementDTO> mapToApiList(List<Element> element);

  List<ElementDTO> map(List<Element> elementList);

  @Mapping(target = "screenNameObj", ignore = true)
  void merge(ElementRequest elementRequest, @MappingTarget Element element);

  @Mapping(target = "screenNameObj", ignore = true)
  Element map(ElementRequest elementRequest);

  @Mapping(target = "screenName", expression = "java(element.getScreenNameObj().equals(null)? null: element.getScreenNameObj().getName())")
  ElementNotificationDTO mapNotificationDTO(Element element);

  List<Element> mapElementsList(List<ElementXMLDTO> readValue);
  List<Element> mapCloudElementsList(List<ElementCloudXMLDTO> readValue);

  Element copy(Element element);
}
