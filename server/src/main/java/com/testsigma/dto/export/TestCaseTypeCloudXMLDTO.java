/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
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
@JsonListRootName(name = "TestCaseTypes")
@JsonRootName(value = "TestCaseType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseTypeCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("DisplayName")
  private String displayName;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("ProjectId")
  private Long projectId;
  @JsonProperty("CreatedById")
  private Long createdById;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  @JsonProperty("UpdatedDate")
  private Timestamp updatedDate;
  @JsonProperty("IsDefault")
  private Boolean isDefault = false;
}
