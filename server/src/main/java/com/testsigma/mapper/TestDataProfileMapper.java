/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.testsigma.dto.TestDataProfileDTO;
import com.testsigma.dto.TestDataSetDTO;
import com.testsigma.dto.export.TestDataCloudXMLDTO;
import com.testsigma.dto.export.TestDataSetCloudXMLDTO;
import com.testsigma.dto.export.TestDataSetXMLDTO;
import com.testsigma.dto.export.TestDataXMLDTO;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.web.request.TestDataProfileRequest;
import com.testsigma.web.request.TestDataSetRequest;
import org.json.JSONObject;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestDataProfileMapper {

  List<TestDataXMLDTO> mapTestData(List<TestData> test);
  List<TestDataCloudXMLDTO> mapToCloudTestData(List<TestData> test);

  @Mapping(target = "data", expression = "java(test.getTempTestData())")
  TestDataXMLDTO mapTestData(TestData test);

  @Mapping(target = "data", expression = "java(test.getTempTestData())")
  TestDataCloudXMLDTO mapToCloudTestData(TestData test);

  List<TestDataSetXMLDTO> mapTestDataSet(List<TestDataSet> test);

  TestDataProfileDTO mapToDTO(TestData testData);

  List<TestDataProfileDTO> mapToDTO(List<TestData> testData);

  List<TestDataSetDTO> mapToDtos(List<TestDataSet> testDataSets);

  @Mapping(target="tempTestData", expression = "java(mapDataSet(request.getData()))")
  TestData map(TestDataProfileRequest request);

  @Mapping(target = "data", expression = "java(testDataSetXMLDTO.getData())")
  TestDataSet map(TestDataSetXMLDTO testDataSetXMLDTO) throws JsonProcessingException;

  @Mapping(target = "data", expression = "java(testDataSetXMLDTO.getData())")
  TestDataSet map2(TestDataSetCloudXMLDTO testDataSetXMLDTO) throws JsonProcessingException;

  List<TestDataSet> map(List<TestDataSetXMLDTO> testDataSetXMLDTO);

  List<TestDataSet> map2(List<TestDataSetCloudXMLDTO> testDataSetXMLDTO);

  default Map<String, TestDataSet> map(TestData testData) {
    Map<String, TestDataSet> testDataSetMap = new HashMap<>();
    if (testData != null) {
      for (TestDataSet testDataSet : testData.getTempTestData()) {
        testDataSetMap.put(testDataSet.getName(), testDataSet);
      }
    }
    return testDataSetMap;
  }


  default TestDataSetDTO map(TestDataSet testDataSet) {
    if (testDataSet == null) {
      return null;
    }

    TestDataSetDTO testDataSetDTO = new TestDataSetDTO();

    if (testDataSet.getId() != null) {
      testDataSetDTO.setId(testDataSet.getId());
    }
    if (testDataSet.getTestDataProfileId() != null) {
      testDataSetDTO.setTestDataProfileId(testDataSet.getTestDataProfileId());
    }
    if (testDataSet.getName() != null) {
      testDataSetDTO.setName(testDataSet.getName());
    }
    if (testDataSet.getDescription() != null) {
      testDataSetDTO.setDescription(testDataSet.getDescription());
    }
    if (testDataSet.getExpectedToFail() != null) {
      testDataSetDTO.setExpectedToFail(testDataSet.getExpectedToFail());
    }
    if (testDataSet.getPosition() != null) {
      testDataSetDTO.setPosition(testDataSet.getPosition());
    }
    if (testDataSet.getData() != null) {
      JSONObject object = testDataSet.getData();
      testDataSetDTO.setData(object);
    }

    return testDataSetDTO;
  }

  default void merge(TestDataProfileRequest testDataProfileRequest, TestData testData) {
    if (testDataProfileRequest == null) {
      return;
    }

    if (testDataProfileRequest.getTestDataName() != null) {
      testData.setTestDataName(testDataProfileRequest.getTestDataName());
    }
    List<TestDataSet> sets = new ArrayList<>();
    if (testDataProfileRequest.getData() != null) {
      sets = mapDataSet(testDataProfileRequest.getData());
    }
    testData.setData(sets);
    testData.setTempTestData(sets);
    testData.setRenamedColumns(testDataProfileRequest.getRenamedColumns());
  }

  List<TestDataSet> mapDataSet(List<TestDataSetRequest> data);

  @Mapping(target = "data", expression = "java(map(testDataXMLDTO.getTestDataSetList()))")
  @Mapping(target = "tempTestData", expression = "java(map(testDataXMLDTO.getTestDataSetList()))")
  TestData mapTestData(TestDataXMLDTO testDataXMLDTO) throws JsonProcessingException;

  @Mapping(target = "data", expression = "java(map2(testDataCloudXMLDTO.getTestDataSetList()))")
  @Mapping(target = "tempTestData", expression = "java(map2(testDataCloudXMLDTO.getTestDataSetList()))")
  TestData mapTestData2(TestDataCloudXMLDTO testDataCloudXMLDTO) throws JsonProcessingException;

    default List<TestData> mapTestDataList(List<TestDataXMLDTO> xmlDTOs) throws JsonProcessingException {
      List<TestData> list = new ArrayList<>();
      for (TestDataXMLDTO testDataXMLDTO : xmlDTOs) {
        list.add(mapTestData(testDataXMLDTO));
      }
      return list;
    }

    default List<TestData> mapCloudTestDataList(List<TestDataCloudXMLDTO> xmlDTOs) throws JsonProcessingException {
      List<TestData> list = new ArrayList<>();
      for (TestDataCloudXMLDTO testDataXMLDTO : xmlDTOs) {
        list.add(mapTestData2(testDataXMLDTO));
      }
      return list;
    }

  TestData copy(TestData testData);
}
