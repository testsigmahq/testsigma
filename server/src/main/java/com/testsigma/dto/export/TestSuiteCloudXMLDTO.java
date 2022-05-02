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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.testsigma.annotation.JsonListRootName;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonListRootName(name = "TestSuites")
@JsonRootName(value = "TestSuite")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestSuiteCloudXMLDTO extends BaseXMLDTO {
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("ActionId")
  private Long actionId;
  @JsonProperty("ApplicationVersionId")
  private Long workspaceVersionId;
  @JsonProperty("PreRequisite")
  private Long preRequisite;
  @JsonProperty("IsManual")
  private boolean isManual;
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
  @JsonProperty("LastRunId")
  private Long lastRunId;
  @JacksonXmlElementWrapper(localName = "Tags")
  @JacksonXmlProperty(localName = "Tag")
  private List<String> tags;
  @JsonProperty("TestCaseIds")
  private List<Long> testCaseIds;
}
