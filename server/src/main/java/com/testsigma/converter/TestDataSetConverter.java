/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.model.TestDataSet;
import com.testsigma.service.ObjectMapperService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Log4j2
@Converter//(autoApply = true)
public class TestDataSetConverter implements AttributeConverter<List<TestDataSet>, String> {
  private static final ObjectMapper om = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(List<TestDataSet> attribute) {
    if (attribute == null)
      return null;
    return new ObjectMapperService().convertToJson(attribute);
  }

  @Override
  public List<TestDataSet> convertToEntityAttribute(String dbData) {
    try {
      if ((dbData == null) || StringUtils.isBlank(dbData)) {
        return null;
      }
      List<TestDataSet> testDataSets = new ArrayList<>();
      for (JsonNode node : om.readTree(dbData)) {

        LinkedHashMap<String, Object> jsonOrderedMap = new LinkedHashMap<>();
        jsonOrderedMap = new ObjectMapperService().parseJson(node.get("data").toString(),
          LinkedHashMap.class);
        JSONObject dataObj = new JSONObject();
        Field map = dataObj.getClass().getDeclaredField("map");
        map.setAccessible(true);//because the field is private final...
        map.set(dataObj, jsonOrderedMap);
        map.setAccessible(false);
        TestDataSet testDataSet = new TestDataSet();
        testDataSet.setName(node.get("name").asText());
        testDataSet.setDescription(node.get("description").asText());
        testDataSet.setExpectedToFail(node.get("expectedToFail").asBoolean());
        testDataSet.setData(dataObj);
        testDataSets.add(testDataSet);
      }
      return testDataSets;
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return null;
    }
  }
}
