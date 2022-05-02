/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "Attachments")
@JsonRootName(value = "Attachment")
public class AttachmentCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("EntityId")
  private Long entityId;
  @JsonProperty("Path")
  private String path;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Entity")
  private String entity;
  @JsonProperty("Type")
  private Integer type = 1;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("CreatedBy")
  private Long createdBy;
  @JsonProperty("UpdatedBy")
  private Long updatedBy;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("DownloadURL")
  private String preSignedURL;
}
