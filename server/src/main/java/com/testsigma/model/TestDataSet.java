/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

@Data
public class TestDataSet {
  private String name;
  private String description;
  private Boolean expectedToFail = false;
  @JsonSerialize(using = JSONObjectSerializer.class)
  private JSONObject data;
}
