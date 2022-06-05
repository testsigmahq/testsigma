/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonListRootName(name = "test-data-sets")
@JsonRootName(value = "test-data-set")
public class TestDataSetXMLDTO extends BaseXMLDTO {
  @JsonProperty("name")
  private String name;
  @JsonProperty("description")
  private String description;
  @JsonProperty("expected-to-fail")
  private Boolean expectedToFail = false;
  @JsonProperty("data")
  private List<Entry> data;

  private Map<String, Object> map = new HashMap<>();

  public void setData(JSONObject data) {
    this.data = new ArrayList();
    data.keySet().forEach((k) -> {
      this.data.add(new Entry(k, data.optString(k)));
    });
  }

  @JsonIgnore
  public JSONObject getData() {
    this.data.forEach((entry) -> {
      map.put(entry.getKey(), entry.getValue());
    });
    return new JSONObject(data);
  }
}

