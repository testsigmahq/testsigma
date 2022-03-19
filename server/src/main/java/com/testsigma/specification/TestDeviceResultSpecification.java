/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestDeviceResult;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDeviceResultSpecification extends BaseSpecification<TestDeviceResult> {

  public TestDeviceResultSpecification(final SearchCriteria criteria) {
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
      case "status":
        if (op == SearchOperation.IN) {
          List<StatusConstant> statusConstants = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            statusConstants.add(StatusConstant.valueOf(string));
          });
          return statusConstants;
        }
        return StatusConstant.valueOf(value.toString());
      default:
        return value;
    }
  }


  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestDeviceResult> root) {
    if (criteria.getKey().equals("targetMachine")) {
      Join s = root.join("testDevice", JoinType.INNER);
      return s.get("agentId");
    } else if (criteria.getKey().equals("environmentName")) {
      Join s = root.join("testDevice", JoinType.INNER);
      return s.get("title");
    } else if (criteria.getKey().equals("childRunId")) {
      Join s = root.join("childResult", JoinType.INNER);
      return s.get("testPlanResultId");
    } else if(criteria.getKey().equals("entityType")) {
      Join s = root.join("testDevice",JoinType.INNER);
      Join s1 = s.join("testPlan",JoinType.INNER);
      return s1.get("entityType");
    }
    return root.get(criteria.getKey());
  }
}
