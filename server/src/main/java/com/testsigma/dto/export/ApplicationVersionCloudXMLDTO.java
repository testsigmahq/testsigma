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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "ApplicationVersions")
@JsonRootName(value = "ApplicationVersion")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationVersionCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("ApplicationId")
  private Long workspaceId;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("VersionName")
  private String versionName;
  @JsonProperty("CustomFields")
  private String customFields;
  @JsonProperty("StartTime")
  private Timestamp startTime;
  @JsonProperty("EndTime")
  private Timestamp endTime;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("UpdatedById")
  private Long updatedById;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("CreatedDate")
  private Timestamp createdDate;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
}
