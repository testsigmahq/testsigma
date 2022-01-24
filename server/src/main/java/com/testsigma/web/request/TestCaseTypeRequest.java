/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.web.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TestCaseTypeRequest {
  private Long id;
  private String name;
  private String displayName;
  private Long workspaceId;
  private Timestamp createdDate;
  private Timestamp updatedDate;
}
