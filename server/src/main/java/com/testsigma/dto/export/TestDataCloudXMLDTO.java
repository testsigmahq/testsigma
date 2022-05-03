/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.converter.StringSetConverter;
import com.testsigma.model.TestDataSet;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Log4j2
@JsonListRootName(name = "TestDatalist")
@JsonRootName(value = "TestData")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDataCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("TestDataSets")
  List<TestDataSetCloudXMLDTO> testDataSets;
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("ApplicationVersionId")
  private Long versionId;
  @JsonProperty("TestDataName")
  private String testDataName;
  @JsonIgnore
  private String data;
  @JsonProperty("Passwords")
  private String passwords;
  @JsonProperty("CopiedFrom")
  private Long copiedFrom;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("createdDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedById")
  private Long updatedById;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonIgnore
  private Map<String, String> renamedColumns;

  public String getData() {
    return this.data;
  }

  public void setData(List<TestDataSet> dataSets) {
    try {

      this.data = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .writeValueAsString(dataSets);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public List<TestDataSetCloudXMLDTO> getTestDataSetList() {
    return testDataSets;
  }

  public List<TestDataSetCloudXMLDTO> getTestDataSets() {
    try {
      if ((this.data == null) || StringUtils.isBlank(this.data)) {
        return null;
      }
      List<TestDataSetCloudXMLDTO> testDataSets = new ArrayList<>();
      for (JsonNode node : new ObjectMapper().readTree(this.data)) {

        Map<String, Object> jsonOrderedMap = new LinkedHashMap<>();
        JsonNode jsonNode = node.get("data");
        jsonNode = jsonNode == null ? node.get("Data") : jsonNode;
        jsonOrderedMap = new ObjectMapperService().parseJson(jsonNode.toString(),
          LinkedHashMap.class);

        JSONObject dataObj = new JSONObject();
        Field map = dataObj.getClass().getDeclaredField("map");
        map.setAccessible(true);//because the field is private final...
        map.set(dataObj, jsonOrderedMap);
        map.setAccessible(false);
        TestDataSetCloudXMLDTO testDataSet = new TestDataSetCloudXMLDTO();
        JsonNode name = node.get("name");
        name = name == null ? node.get("Name") : name;
        testDataSet.setName(name.asText());
        JsonNode description = node.get("description");
        description = description == null ? node.get("Description") : description;
        testDataSet.setDescription(description.asText());
        JsonNode expectedToFail = node.get("expectedToFail");
        expectedToFail = expectedToFail == null ? node.get("ExpectedToFail") : expectedToFail;
        testDataSet.setExpectedToFail(expectedToFail.asBoolean());
        testDataSet.setData(dataObj);
        testDataSets.add(testDataSet);
      }
      this.testDataSets = testDataSets;
      return testDataSets;
    } catch (Exception ex) {
      return null;
    }
  }

  public void setTestDataSets(List<TestDataSetCloudXMLDTO> dataSets) {
    try {

      dataSets.forEach(data -> {
        List<Entry> dataMap = data.getDataMap();
        JSONObject object = new JSONObject();
        for (Entry entry : dataMap) {
          object.put(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
        }
        data.setData(object);
      });

      ObjectMapper mapper = new ObjectMapper();
      SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
        .serializeAllExcept("dataMap");
      FilterProvider filters = new SimpleFilterProvider()
        .addFilter("myFilter", theFilter);

      this.testDataSets = dataSets;
      this.data = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .writer(filters).writeValueAsString(dataSets);

    } catch (Exception e) {
      log.error(e, e);
      e.printStackTrace();
    }

  }

  public List<String> getPasswords() {
    return new StringSetConverter().convertToEntityAttribute(this.passwords);
  }

  public void setPasswords(List<String> passwordList) {
    if (passwordList != null) {
      this.passwords = new StringSetConverter().convertToDatabaseColumn(passwordList);
    }
  }

}
