/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.web.request;

import com.testsigma.model.ReRunType;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import lombok.Data;

import java.util.Map;

@Data
public class TestPlanResultRequest {
  private Long testPlanId;
  private String buildNo;
  private Map<String, Object> runtimeData;
  private ResultConstant result;
  private StatusConstant status;
  private Boolean isReRun = false;
  private ReRunType reRunType = ReRunType.NONE;
  private Long parenttestPlanResultId;
}
