/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.UploadStatus;
import com.testsigma.model.UploadType;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "uploads")
@JsonRootName(value = "upload")
public class UploadXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("project-id")
  private Long projectId;
  @JsonProperty("workspace-id")
  private Long workspaceId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("app-path")
  private String appPath;
  @JsonProperty("file-name")
  private String fileName;
  @JsonProperty("upload-type")
  private UploadType uploadType;
  @JsonProperty("version")
  private String version;
  @JsonProperty("is-public")
  private Boolean isPublic;
  @JsonProperty("upload-status")
  private UploadStatus uploadStatus;
  @JsonProperty("comments")
  private String comments;
  @JsonProperty("message")
  private String message;
  @JsonProperty("status")
  private Integer status;
  @JsonProperty("name-from-app")
  private String nameFromApp;
  @JsonProperty("version-from-app")
  private String versionFromApp;
  @JsonProperty("file-size")
  private Integer fileSize;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("updated-date")
  private Timestamp updatedDate;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("updated-by-id")
  private Long updatedById;
  @JsonProperty("last-uploaded-time")
  private Timestamp lastUploadedTime;
  @JsonProperty("pre-signed-url")
  private String preSignedURL;
}
