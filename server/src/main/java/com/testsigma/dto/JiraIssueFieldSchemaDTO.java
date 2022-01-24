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

@Data
public class JiraIssueFieldSchemaDTO {
  String type;
  String system;
  String custom;
  String items;
}
