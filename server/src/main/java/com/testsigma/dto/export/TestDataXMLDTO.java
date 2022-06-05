/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.converter.StringSetConverter;
import com.testsigma.model.TestDataSet;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonListRootName(name = "test-data-list")
@JsonRootName(value = "test-data")
public class TestDataXMLDTO extends BaseXMLDTO {
  @JsonProperty("test-data-sets")
  List<TestDataSetXMLDTO> testDataSets;
  @JsonProperty("id")
  private Long id;
  @JsonProperty("application-version-id")
  private Long versionId;
  @JsonProperty("test-data-name")
  private String testDataName;
  @JsonIgnore
  private String data;

  @JsonProperty("copied-from")
  private Long copiedFrom;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-by-id")
  private Long updatedById;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonIgnore
  private Map<String, String> renamedColumns;

  public void setData(List<TestDataSet> dataSets) {
    try {
      this.data = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .writeValueAsString(dataSets);
      ;
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public List<TestDataSetXMLDTO> getTestDataSetList() {
    return testDataSets;
  }


  public List<TestDataSetXMLDTO> getTestDataSets() {
    try {
      if ((this.data == null) || StringUtils.isBlank(this.data)) {
        return null;
      }
      List<TestDataSetXMLDTO> testDataSets = new ArrayList<>();
      for (JsonNode node : new ObjectMapper().readTree(this.data)) {

        Map<String, Object> jsonOrderedMap = new LinkedHashMap<>();
        jsonOrderedMap = new ObjectMapperService().parseJson(node.get("data").toString(),
          LinkedHashMap.class);
        JSONObject dataObj = new JSONObject();
        Field map = dataObj.getClass().getDeclaredField("map");
        map.setAccessible(true);//because the field is private final...
        map.set(dataObj, jsonOrderedMap);
        map.setAccessible(false);
        TestDataSetXMLDTO testDataSet = new TestDataSetXMLDTO();
        testDataSet.setName(node.get("name").asText());
        testDataSet.setDescription(node.get("description").asText());
        testDataSet.setExpectedToFail(node.get("expectedToFail").asBoolean());
        testDataSet.setData(dataObj);
        testDataSets.add(testDataSet);
      }
      this.testDataSets = testDataSets;
      return testDataSets;
    } catch (Exception ex) {
      return null;
    }
  }

  public void setTestDataSets(List<TestDataSetXMLDTO> dataSets) {
    try {
      this.data = new ObjectMapper()
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .writeValueAsString(dataSets);
      ;
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
