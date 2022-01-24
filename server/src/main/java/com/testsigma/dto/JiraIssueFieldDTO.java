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

import java.util.List;

@Data
public class JiraIssueFieldDTO {
  String name;
  String key;
  Boolean hasDefaultValue;
  Boolean required;
  String autoCompleteUrl;
  JiraIssueFieldSchemaDTO schema;
  List<JiraFieldAllowedValue> allowedValues;
}
