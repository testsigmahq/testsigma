/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestCaseDataDrivenResult;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

public class TestCaseDataDrivenResultSpecification extends BaseSpecification<TestCaseDataDrivenResult> {

  public TestCaseDataDrivenResultSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestCaseDataDrivenResult> root) {
    if (criteria.getKey().equals("childRunId")) {
      Join s = root.join("iterationResult", JoinType.INNER);
      Join s1 = s.join("childResult", JoinType.INNER);
      return s1.get("testPlanResultId");
    }
    return root.get(criteria.getKey());
  }
}
