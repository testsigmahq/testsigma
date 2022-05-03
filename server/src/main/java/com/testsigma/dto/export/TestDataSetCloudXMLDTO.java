/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonListRootName(name = "TestDataSets")
@JsonRootName(value = "TestDataset")
@JsonFilter("myFilter")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestDataSetCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("ExpectedToFail")
  private Boolean expectedToFail = false;

  @JacksonXmlElementWrapper(localName = "DataMap")
  @JacksonXmlProperty(localName = "DataEntry")
  private List<Entry> dataMap = new ArrayList();

  private Map<String, Object> data = new HashMap<>();

  @JsonIgnore
  public JSONObject getData() {
    this.dataMap.forEach((entry) -> {
      data.put(entry.getKey(), entry.getValue());
    });
    return new JSONObject(data);
  }

  public void setData(JSONObject data) {
    data.keySet().forEach((k) -> {
      this.dataMap.add(new Entry(k, data.optString(k, "")));
    });
  }

}

