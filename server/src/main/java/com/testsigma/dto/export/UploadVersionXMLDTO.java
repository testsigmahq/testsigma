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
import com.testsigma.model.UploadType;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonListRootName(name = "Uploads")
@JsonRootName(value = "Upload")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadVersionXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("UploadId")
  private String uploadId;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Path")
  private String path;
  @JsonProperty("FileName")
  private String fileName;
  @JsonProperty("UploadType")
  private UploadType uploadType;
  @JsonProperty("FileSize")
  private Long fileSize;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("UpdatedDate")
  private Timestamp updatedDate;
  @JsonProperty("DownloadURL")
  private String downloadURL;
}
