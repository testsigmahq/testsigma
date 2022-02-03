/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestSuiteResult;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSuiteResultSpecification extends BaseSpecification<TestSuiteResult> {

  public TestSuiteResultSpecification(final SearchCriteria criteria) {
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
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestSuiteResult> root) {
    if (criteria.getKey().equals("suiteName")) {
      Join s = root.join("testSuite", JoinType.INNER);
      return s.get("name");
    } else if (criteria.getKey().equals("childRunId")) {
      Join s = root.join("childResult", JoinType.INNER);
      return s.get("testPlanResultId");
    }
    return root.get(criteria.getKey());
  }
}
