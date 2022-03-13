/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestSuiteEntityDTO {
  private Long id;
  private String name;
  private Long resultId;
  private Long groupResultId;
  private Long preRequisite;
  private Long environmentResultId;
  private Long testPlanResultId;
  private List<TestCaseEntityDTO> testCases;
}
