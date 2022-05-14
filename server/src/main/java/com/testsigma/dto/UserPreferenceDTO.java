/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserPreferenceDTO {
  private Long testCaseFilterId;
  private Long versionId;
  private Long workspaceId;
  private Timestamp createdDate;
  private Timestamp updatedDate;
  private boolean showedGitHubStar;
}
