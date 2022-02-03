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
@JsonListRootName(name = "screen-name")
@JsonRootName(value = "screen-name")
public class ElementScreenNameXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("application-version-id")
  private Long workspaceVersionId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("created-by-id")
  private Long createdById;
  @JsonProperty("updated-by-id")
  private Long updatedById;
}
