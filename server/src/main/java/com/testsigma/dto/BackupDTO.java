/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.dto;

import lombok.Data;

import java.io.File;

@Data
public class BackupDTO {
  private Long id;
  private Boolean isTestCaseEnabled;
  private Boolean isTestStepEnabled;
  private Boolean isRestStepEnabled;
  private Boolean isUploadsEnabled;
  private Boolean isTestCasePriorityEnabled;
  private Boolean isTestCaseTypeEnabled;
  private Boolean isElementEnabled;
  private Boolean isElementScreenNameEnabled;
  private Boolean isTestDataEnabled;
  private Boolean isAttachmentEnabled;
  private Boolean isAgentEnabled;
  private Boolean isTestPlanEnabled;
  private Boolean isTestDeviceEnabled;
  private Boolean isSuitesEnabled;
  private Boolean isLabelEnabled;
  private Long workspaceVersionId;
  private String backupURL;
  private String name;
  private String entity;
  private String application;
  private String version;
  private Long filterId;
  private String versionURL;
  private File srcFiles;
  private File destFiles;
}
