/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import lombok.Data;

import java.util.Map;

@Data
public class TestCaseResultExternalMappingRequest {

  Long id;
  Long testCaseResultId;
  Long workspaceId;
  Map<String, Object> fields;
  Boolean linkToExisting;
  String externalId;
}
