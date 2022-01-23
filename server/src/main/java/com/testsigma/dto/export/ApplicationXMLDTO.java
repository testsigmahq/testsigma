/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "applications")
@JsonRootName(value = "workspace")
public class ApplicationXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("description")
  private String description;
  @JsonProperty("project-id")
  private Long projectId;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("updated-by-id")
  private Long updatedById;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("created-date")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("updated-date")
  private Timestamp updatedDate;
  @JsonProperty("application-type")
  private WorkspaceType workspaceType;
  @JsonProperty("custom-fields")
  private String customFields;
}
