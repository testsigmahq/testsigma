/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class WorkspaceRequest {
  private Long id;
  private String name;
  private String description;
  private WorkspaceType workspaceType;
  private List<WorkspaceVersionRequest> workspaceVersions;
}
