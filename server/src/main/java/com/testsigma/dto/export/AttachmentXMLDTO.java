/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
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

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "attachments")
@JsonRootName(value = "attachment")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("entity-id")
  private Long entityId;
  @JsonProperty("path")
  private String path;
  @JsonProperty("name")
  private String name;
  @JsonProperty("entity")
  private String entity;
  @JsonProperty("type")
  private Integer type = 1;
  @JsonProperty("description")
  private String description;
  @JsonProperty("created-by")
  private Long createdBy;
  @JsonProperty("updated-by")
  private Long updatedBy;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("presigned-url")
  private String preSignedURL;
}
