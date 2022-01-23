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
public class JiraIssueTypeDTO {
  String id;
  String description;
  String iconUrl;
  String name;
  String key;
  Map<String, JiraIssueFieldDTO> fields;
}
