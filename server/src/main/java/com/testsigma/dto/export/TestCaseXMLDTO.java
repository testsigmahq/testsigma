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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.TestCaseStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonListRootName(name = "test-cases")
@JsonRootName(value = "test-case")
public class TestCaseXMLDTO extends BaseXMLDTO {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("comments")
  private String comments;
  @JsonProperty("start-time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp startTime;
  @JsonProperty("end-time")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp endTime;
  @JsonProperty("is-data-driven")
  private Boolean isDataDriven;
  @JsonProperty("is-reviewed")
  private Boolean isReviewed;
  @JsonProperty("is-step-group")
  private Boolean isStepGroup;
  @JsonProperty("priority")
  private Long priority;
  @JsonProperty("requirement-id")
  private Long requirementId;
  @JsonProperty("reviewed-by")
  private Long reviewedBy;
  @JsonProperty("description")
  private String description;
  @JsonProperty("name")
  private String name;
  @JsonProperty("status")
  private TestCaseStatus status;
  @JsonProperty("type")
  private Long type;
  @JsonProperty("test-data-id")
  private Long testDataId;
  @JsonProperty("user-id")
  private Long userId;
  @JsonProperty("application-version-id")
  private Long workspaceVersionId;
  @JsonProperty("custom-fields")
  private String customFields;
  @JsonProperty("pre-requisite")
  private Long preRequisite;
  @JsonProperty("assignee")
  private Long assignee;
  @JsonProperty("testcase-key")
  private String testcaseKey;
  @JsonProperty("deleted")
  private Boolean deleted;
  @JsonProperty("test-data-index")
  private Integer testDataStartIndex;
  @JsonProperty("test-data-end-index")
  private Integer testDataEndIndex;
  @JsonProperty("reviewed-at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp reviewedAt;
  @JsonProperty("review-submitted-at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp reviewSubmittedAt;
  @JsonProperty("review-submitted-by")
  private Long reviewSubmittedBy;
  @JsonProperty("draft-at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp draftAt;
  @JsonProperty("draft-by")
  private Long draftBy;
  @JsonProperty("obsolete-at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp obsoleteAt;
  @JsonProperty("obsolete-by")
  private Long obsoleteBy;
  @JsonProperty("ready-at")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp readyAt;
  @JsonProperty("ready-by")
  private Long readyBy;
  @JsonProperty("last-run-id")
  private Long lastRunId;
  @JsonProperty("created-by")
  private Long createdBy;
  @JsonProperty("created-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("updated-by")
  private Long updatedBy;
  @JsonProperty("updated-date")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("priority-name")
  private String priorityName;
  @JsonProperty("status-name")
  private String statusName;
  @JsonProperty("type-name")
  private String typeName;
  @JsonProperty("test-data-name")
  private String testDataName;
  @JsonProperty("pre-requisite-name")
  private String preRequisiteName;
  @JsonProperty("order")
  private String order;
}
