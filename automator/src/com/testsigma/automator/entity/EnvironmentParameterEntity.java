/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.automator.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.testsigma.automator.deserialize.JSONObjectDeserialize;
import lombok.Data;
import org.json.JSONObject;

@Data
public class EnvironmentParameterEntity {
  private Long id;
  private String name;
  private String description;
  @JsonDeserialize(using = JSONObjectDeserialize.class)
  private JSONObject parameters;
}
