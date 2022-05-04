/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

import javax.persistence.Column;
import java.sql.Timestamp;

@Data
@JsonListRootName(name = "Uploads")
@JsonRootName(value = "Upload")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("ApplicationId")
  private Long workspaceId;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("LatestVersionId")
  private Long latestVersionId;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("UpdatedDate")
  private Timestamp updatedDate;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("UpdatedById")
  private Long updatedById;
}
