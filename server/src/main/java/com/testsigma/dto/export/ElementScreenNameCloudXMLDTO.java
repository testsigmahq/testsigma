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
@JsonListRootName(name = "ScreenName")
@JsonRootName(value = "ScreenName")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementScreenNameCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("ApplicationVersionId")
  private Long workspaceVersionId;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("UpdatedById")
  private Long updatedById;
}
