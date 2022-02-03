/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class WorkspaceDTO {
  private Long id;
  private String name;
  private String description;
  @JsonProperty("is_demo")
  private Boolean isDemo;
  private WorkspaceType workspaceType;
  private Timestamp createdDate;
  private Timestamp updatedDate;
}
