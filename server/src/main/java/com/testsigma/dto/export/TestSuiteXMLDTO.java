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
import java.util.List;

@Data
@JsonListRootName(name = "test-suites")
@JsonRootName(value = "test-suite")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestSuiteXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("name")
  private String name;
  @JsonProperty("description")
  private String description;
  @JsonProperty("action-id")
  private Long actionId;
  @JsonProperty("application-version-id")
  private Long workspaceVersionId;
  @JsonProperty("pre-requisite")
  private Long preRequisite;
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
  @JsonProperty("last-run-id")
  private Long lastRunId;
  @JsonProperty("tags")
  private List<String> tags;
  @JsonProperty("test-case-ids")
  private List<Long> testCaseIds;
}
