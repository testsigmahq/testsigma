/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.testsigma.serializer.JSONObjectSerializer;
import lombok.Data;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.List;

@Data
public class EnvironmentDTO {
  private Long id;
  private String name;
  private String description;
  private Timestamp updatedDate;
  private Timestamp createdDate;
  @JsonSerialize(using = JSONObjectSerializer.class)
  private JSONObject parameters;
}
