/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class WorkspaceVersionRequest {
  private Long id;
  private Long workspaceId;
  private String versionName;
  private String description;
}
