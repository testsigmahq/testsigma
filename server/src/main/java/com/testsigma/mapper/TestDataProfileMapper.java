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
import com.testsigma.util.EncryptDecryt;
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
    List<String> passwords = testData.getPasswords();
    passwords = (passwords != null) ? passwords : new ArrayList<String>();
    if (testData != null) {
      for (TestDataSet testDataSet : testData.getData()) {
        for (String password : passwords) {
          testDataSet.getData().put(password, new EncryptDecryt().decrypt(testDataSet.getData().getString(password)));
        }
        testDataSetMap.put(testDataSet.getName(), testDataSet);
      }
    }
    return testDataSetMap;
  }

  default List<TestDataSetDTO> testDataSetListToTestDataSetDTOList(List<TestDataSet> list, List<String> passwords) {
    if (list == null) {
      return null;
    }

    List<TestDataSetDTO> list1 = new ArrayList<TestDataSetDTO>(list.size());
    for (TestDataSet testDataSet : list) {
      list1.add(map(testDataSet, passwords));
    }

    return list1;
  }

  default TestDataSetDTO map(TestDataSet testDataSet, List<String> passwords) {
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
      for (String password : passwords) {
        object.put(password, new EncryptDecryt().decrypt(object.getString(password)));
      }
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

    List<String> passwords = testData.getPasswords();
    Map<String, String> renamedColumns = testDataProfileRequest.getRenamedColumns();
    if (passwords != null) {
      testData.setPasswords(passwords);
      int index = 0;
      for (TestDataSet set : sets) {
        for (String password : passwords) {
          if (testDataProfileRequest.getPasswords().indexOf(password) > -1 && testData.getData().size() >= index + 1
            && testData.getData().get(index).getData().getString(password).equals(set.getData().getString(password))) {
            set.getData().put(password, new EncryptDecryt().decrypt(set.getData().getString(password)));
          } else if (renamedColumns.containsKey(password) &&
            testDataProfileRequest.getPasswords().indexOf(renamedColumns.get(password)) > -1) {
            set.getData().put(renamedColumns.get(password), new EncryptDecryt().decrypt(set.getData().getString(renamedColumns.get(password))));
          } else if (!testDataProfileRequest.getData().get(index).getData().getString(password).equals(set.getData().getString(password))) {
            set.getData().put(password, new EncryptDecryt().encrypt(set.getData().getString(password)));
          }
        }
        index++;
      }
    }

    testData.setData(sets);
    testData.setPasswords(testDataProfileRequest.getPasswords());
    testData.setRenamedColumns(testDataProfileRequest.getRenamedColumns());
  }

  List<TestDataSet> mapDataSet(List<TestDataSetRequest> data);

}
