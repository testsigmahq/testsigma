/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.IntegrationMetaData;
import com.testsigma.model.Integration;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class IntegrationsDTO {
  @JsonProperty("id")
  private String id;

  private String name;

  @JsonProperty("username")
  private String username;


  private String password;

  private String token;

  private String description;

  @JsonProperty("workspace")
  private Integration workspace;

  private String url;

  private IntegrationMetaData metadata;

  private Timestamp createdDate;
  private Timestamp updatedDate;

}
