/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.automator.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Data
public class TestCaseEntity implements Cloneable, Serializable {
  private Long id;
  private String testCaseName;
  private Boolean isStepGroup = false;
  private Boolean expectedToFail = false;
  private Boolean isDataDriven = false;
  private String testDataSetName;
  private Calendar startTime;
  private Calendar endTime;
  private Long preRequisite;
  private Long groupResultId;
  private Long testCaseResultId;
  private Integer testDataStartIndex;
  private ResultConstant result;
  private Long testDataId;
  private List<TestCaseStepEntity> testSteps = new ArrayList<TestCaseStepEntity>();
  private List<TestCaseEntity> dataDrivenTestCases = new ArrayList<TestCaseEntity>();
  private Integer errorCode;
  private String message;
  private Long phoneNumberId;
  private String videoPreSignedURL;
  private String seleniumLogPreSignedURL;
  private String consoleLogPreSignedURL;
  private String harLogPreSignedURL;
  private String appiumLogPreSignedURL;
  private String parentHierarchy;
}
