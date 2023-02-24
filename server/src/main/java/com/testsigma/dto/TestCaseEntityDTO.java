/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.dto;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;

import java.util.*;

@Data
public class TestCaseEntityDTO implements Cloneable {
  private Long id;
  private String testCaseName;
  private Boolean isStepGroup;
  private Boolean expectedToFail;
  private Boolean isDataDriven;
  private String testDataSetName;
  private String testDataProfileName;
  private Integer testDataIndex;
  private Calendar startTime;
  private Calendar endTime;
  private Long preRequisite;
  private Long groupResultId;
  private Long testCaseResultId;
  private Integer testDataStartIndex;
  private StatusConstant status;
  private ResultConstant result;
  private Long testDataId;
  private List<TestCaseStepEntityDTO> testSteps = new ArrayList<>();
  private List<TestCaseEntityDTO> dataDrivenTestCases = new ArrayList<>();
  private Integer errorCode;
  private String message;
  private Long phoneNumberId;
  private Boolean visualEnabled = false;
  private String parentHierarchy;
  private Map<Long, Long> stepGroupParentForLoopStepIdTestDataSetMap;

  public TestCaseEntityDTO clone() throws CloneNotSupportedException {
    TestCaseEntityDTO entity = (TestCaseEntityDTO) super.clone();
    List<TestCaseStepEntityDTO> steps = new ArrayList<>();
    for (TestCaseStepEntityDTO step : testSteps) {
      steps.add(step.clone());
    }
    entity.setTestSteps(steps);
    return entity;

  }

  public Map<Long, Long> getStepGroupParentForLoopStepIdTestDataSetMap(){
    return stepGroupParentForLoopStepIdTestDataSetMap != null ? stepGroupParentForLoopStepIdTestDataSetMap : new HashMap<>();
  }

}
