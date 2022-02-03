/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.TestStepPriority;
import com.testsigma.model.TestStepResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestStepResultSpecification extends BaseSpecification<TestStepResult> {

  public TestStepResultSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "result":
        if (op == SearchOperation.IN) {
          List<ResultConstant> resultConstants = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            resultConstants.add(ResultConstant.valueOf(string));
          });
          return resultConstants;
        }
        return ResultConstant.valueOf(value.toString());
      case "priority":
        if (op == SearchOperation.IN) {
          List<TestStepPriority> resultStatuses = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            resultStatuses.add(TestStepPriority.valueOf(string));
          });
          return resultStatuses;
        }
        return TestStepPriority.valueOf(value.toString());
      default:
        return value;
    }
  }
}
