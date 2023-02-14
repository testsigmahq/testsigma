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
import com.testsigma.dto.export.ElementMetaDataCloudXMLDTO;
import com.testsigma.dto.export.ElementXMLDTO;
import com.testsigma.model.Element;
import com.testsigma.model.ElementMetaData;
import com.testsigma.model.ElementMetaDataRequest;
import com.testsigma.web.request.ElementRequest;
import com.testsigma.web.request.testproject.TestProjectElementRequest;
import org.json.JSONArray;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ElementMapper {

  List<ElementXMLDTO> mapElements(List<Element> elementsList);
  List<ElementCloudXMLDTO> mapToCloudElements(List<Element> elementsList);
  
  @Mapping(target = "cloudMetadata", expression = "java(mapToCloudMetadata(element.getMetadata()))")
  ElementCloudXMLDTO mapToCloudElement(Element element);

  default ElementMetaDataCloudXMLDTO mapToCloudMetadata(ElementMetaData elementMetaData) {
    ElementMetaDataCloudXMLDTO metaDataCloudXMLDTO = new ElementMetaDataCloudXMLDTO();
    if (elementMetaData == null) {
      return metaDataCloudXMLDTO;
    }
    metaDataCloudXMLDTO.setXPath(elementMetaData.getXPath());
    metaDataCloudXMLDTO.setCurrentElement(elementMetaData.getCurrentElement());
    if (elementMetaData.getTestData() != null) {
      JSONArray jsonArray = new JSONArray();
      jsonArray.put(elementMetaData.getTestData());
      metaDataCloudXMLDTO.setTestData(jsonArray);
    } else {
      metaDataCloudXMLDTO.setTestData(new JSONArray());
    }
    return metaDataCloudXMLDTO;
  }

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

  @Mapping(target = "locatorValue", expression = "java(testProjectElementRequest.getLocators().get(0).getValue())")
  @Mapping(target = "type", expression = "java(testProjectElementRequest.getLocators().get(0).getLocatorType().getId())")
  Element map(TestProjectElementRequest testProjectElementRequest);

  @Mapping(target = "screenName", expression = "java(element.getScreenNameObj().equals(null)? null: element.getScreenNameObj().getName())")
  ElementNotificationDTO mapNotificationDTO(Element element);

  List<Element> mapElementsList(List<ElementXMLDTO> readValue);
  List<Element> mapCloudElementsList(List<ElementCloudXMLDTO> readValue);

  Element copy(Element element);
}
