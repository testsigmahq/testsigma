/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.testsigma.serializer.JSONObjectDeserializer;
import lombok.Data;
import org.json.JSONObject;

import java.util.List;

@Data
public class EnvironmentRequest {
  private Long id;
  private String name;
  private String description;
  @JsonDeserialize(using = JSONObjectDeserializer.class)
  private JSONObject parameters;
  private List<String> passwords;
}
