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

import java.util.Map;

@Data
public class TestCaseResultExternalMappingDTO {

  Long id;
  Long testCaseResultId;
  Long workspaceId;
  String externalId;
  Map<String, Object> fields;
}
