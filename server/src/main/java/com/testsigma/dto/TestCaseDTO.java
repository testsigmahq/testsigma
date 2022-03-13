package com.testsigma.dto;
/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import com.testsigma.model.Attachment;
import com.testsigma.model.TestCaseResult;
import com.testsigma.model.TestCaseStatus;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Data
public class TestCaseDTO {

  TestCaseDTO preRequisiteCase;
  private Long id;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private Timestamp startTime;
  private Timestamp endTime;
  private Boolean isDataDriven;
  private Boolean isStepGroup;
  private Long priority;
  private Timestamp draftAt;
  private Timestamp obsoleteAt;
  private Timestamp readyAt;
  private String description;
  private String name;
  private TestCaseStatus status;
  private Long type;
  private Long testDataId;
  private Long workspaceVersionId;
  private Long preRequisite;
  private Long copiedFrom;
  private Boolean deleted;
  private Integer testDataStartIndex;
  private Integer testDataEndIndex;
  private Set<TestCaseResult> results;
  private String priorityName;
  private String typeName;
  private String testDataName;
  private String preRequisiteName;
  private String order;
  private List<Attachment> files;
  private List<String> tags;
  private WorkspaceVersionDTO version;
  private String from;
  private String to;
  private String url;
  private TestDataProfileDTO testData;
  private TestCaseResultDTO lastRun;
}
