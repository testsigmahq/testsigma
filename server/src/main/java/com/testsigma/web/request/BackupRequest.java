/*
 *****************************************************************************
 * Copyright (C) 2019 Testsigma Technologies Inc.
 * All rights reserved.
 ****************************************************************************
 */
package com.testsigma.web.request;

import lombok.Data;

@Data
public class BackupRequest {
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
  private Boolean isRequirementEnabled;
  private Boolean isTestPlanEnabled;
  private Boolean isTestDeviceEnabled;
  private Boolean isSuitesEnabled;
  private Boolean isLabelEnabled;
  private Long filterId;
  private Long workspaceVersionId;
}
