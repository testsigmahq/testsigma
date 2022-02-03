/*
 * *****************************************************************************
 *  Copyright (C) 2019 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TestCasePriorityDTO {
  private Long id;
  private String name;
  private String displayName;
  private Timestamp createdDate;
  private Timestamp updatedDate;
}
