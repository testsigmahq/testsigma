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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.testsigma.annotation.JsonListRootName;
import com.testsigma.model.TestCaseStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@JsonListRootName(name = "TestCases")
@JsonRootName(value = "TestCase")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestCaseCloudXMLDTO extends BaseXMLDTO {
  @JacksonXmlElementWrapper(localName = "Tags")
  @JacksonXmlProperty(localName = "Tag")
  List<String> tags;
  @JsonProperty("Id")
  private Long id;
  @JsonProperty("Comments")
  private String comments;
  @JsonProperty("StartTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp startTime;
  @JsonProperty("EndTime")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp endTime;
  @JsonProperty("IsDataDriven")
  private Boolean isDataDriven;
  @JsonProperty("IsReviewed")
  private Boolean isReviewed;
  @JsonProperty("IsTestComponent")
  private Boolean isStepGroup;
  @JsonProperty("Priority")
  private Long priority;
  @JsonProperty("RequirementId")
  private Long requirementId;
  @JsonProperty("ReviewedBy")
  private Long reviewedBy;
  @JsonProperty("Description")
  private String description;
  @JsonProperty("Name")
  private String name;
  @JsonProperty("Status")
  private TestCaseStatus status;
  @JsonProperty("Type")
  private Long type;
  @JsonProperty("TestDataId")
  private Long testDataId;
  @JsonProperty("UserId")
  private Long userId;
  @JsonProperty("ApplicationVersionId")
  private Long workspaceVersionId;
  @JsonProperty("CustomFields")
  private String customFields;
  @JsonProperty("PreRequisite")
  private Long preRequisite;
  @JsonProperty("IsActive")
  private Boolean isActive;
  @JsonProperty("Assignee")
  private Long assignee;
  @JsonProperty("TestcaseKey")
  private String testcaseKey;
  @JsonProperty("IsManual")
  private Boolean isManual;
  @JsonProperty("Deleted")
  private Boolean deleted;
  @JsonProperty("TestDataIndex")
  private Integer testDataStartIndex;
  @JsonProperty("TestDataEndIndex")
  private Integer testDataEndIndex;
  @JsonProperty("ReviewedAt")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp reviewedAt;
  @JsonProperty("ReviewSubmittedAt")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp reviewSubmittedAt;
  @JsonProperty("ReviewSubmittedBy")
  private Long reviewSubmittedBy;
  @JsonProperty("DraftAt")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp draftAt;
  @JsonProperty("DraftBy")
  private Long draftBy;
  @JsonProperty("ObsoleteAt")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp obsoleteAt;
  @JsonProperty("ObsoleteBy")
  private Long obsoleteBy;
  @JsonProperty("ReadyAt")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp readyAt;
  @JsonProperty("ReadyBy")
  private Long readyBy;
  @JsonProperty("LastRunId")
  private Long lastRunId;
  @JsonProperty("CreatedBy")
  private Long createdById;
  @JsonProperty("CreatedDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp createdDate;
  @JsonProperty("UpdatedBy")
  private Long updatedById;
  @JsonProperty("UpdateDate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
  private Timestamp updatedDate;
  @JsonProperty("PriorityName")
  private String priorityName;
  @JsonProperty("StatusName")
  private String statusName;
  @JsonProperty("TypeName")
  private String typeName;
  @JsonProperty("TestDataName")
  private String testDataName;
  @JsonProperty("PreRequisiteName")
  private String preRequisiteName;
  @JsonProperty("AssigneeName")
  private String assigneeName;
  @JsonProperty("Order")
  private String order;
  @JsonProperty("TestCaseDataDrivenCondition")
  private TestCaseDataDrivenConditionCloudXMLDTO testCaseDataDrivenCondition;
}
