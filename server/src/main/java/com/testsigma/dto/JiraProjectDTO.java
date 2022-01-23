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
import java.util.Map;

@Data
public class JiraProjectDTO {
  String name;
  String id;
  String key;
  Map<String, String> avatarUrls;
  List<JiraIssueTypeDTO> issuetypes;
}
