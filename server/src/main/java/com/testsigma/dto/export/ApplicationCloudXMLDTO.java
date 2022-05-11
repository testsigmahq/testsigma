/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
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
@JsonListRootName(name = "Applications")
@JsonRootName(value = "Application")
public class ApplicationCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("ProjectId")
  private Long projectId;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("UpdatedById")
  private Long updatedById;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("CreatedDate")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("UpdatedDate")
  private Timestamp updatedDate;
  @JsonProperty("ApplicationType")
  private WorkspaceType workspaceType;
  @JsonProperty("CustomFields")
  private String customFields;
}
