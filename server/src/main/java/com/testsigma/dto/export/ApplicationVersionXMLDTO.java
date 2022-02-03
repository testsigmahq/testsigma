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
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "workspace-versions")
@JsonRootName(value = "workspace-version")
public class ApplicationVersionXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("workspace-id")
  private Long workspaceId;
  @JsonProperty("description")
  private String description;
  @JsonProperty("version-name")
  private String versionName;
  @JsonProperty("custom-fields")
  private String customFields;
  @JsonProperty("start-time")
  private Timestamp startTime;
  @JsonProperty("end-time")
  private Timestamp endTime;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("updated-by-id")
  private Long updatedById;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("created-date")
  private Timestamp createdDate;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
}
