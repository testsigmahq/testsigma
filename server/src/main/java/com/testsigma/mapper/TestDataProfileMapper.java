/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.mapper;

import com.testsigma.dto.TestDataProfileDTO;
import com.testsigma.dto.TestDataSetDTO;
import com.testsigma.dto.export.TestDataSetXMLDTO;
import com.testsigma.dto.export.TestDataXMLDTO;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataSet;
import com.testsigma.web.request.TestDataProfileRequest;
import com.testsigma.web.request.TestDataSetRequest;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
  nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface TestDataProfileMapper {
  List<TestDataXMLDTO> mapTestData(List<TestData> test);

  List<TestDataSetXMLDTO> mapTestDataSet(List<TestDataSet> test);

  TestDataProfileDTO mapToDTO(TestData testData);

  List<TestDataProfileDTO> mapToDTO(List<TestData> testData);

  TestData map(TestDataProfileRequest request);

  default Map<String, TestDataSet> map(TestData testData) {
    Map<String, TestDataSet> testDataSetMap = new HashMap<>();
    if (testData != null) {
      for (TestDataSet testDataSet : testData.getData()) {
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

    if (testDataSet.getName() != null) {
      testDataSetDTO.setName(testDataSet.getName());
    }
    if (testDataSet.getDescription() != null) {
      testDataSetDTO.setDescription(testDataSet.getDescription());
    }
    if (testDataSet.getExpectedToFail() != null) {
      testDataSetDTO.setExpectedToFail(testDataSet.getExpectedToFail());
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
    testData.setRenamedColumns(testDataProfileRequest.getRenamedColumns());
  }

  List<TestDataSet> mapDataSet(List<TestDataSetRequest> data);

}
