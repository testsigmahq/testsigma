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
@JsonListRootName(name = "TestCasePriorities")
@JsonRootName(value = "TestCasePriority")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCasePriorityCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("DisplayName")
  private String displayName;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("ProjectId")
  private Long projectId;
  @JsonProperty("CreatedIyId")
  private Long createdById;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("IsDefault")
  private Boolean isDefault = false;
}
