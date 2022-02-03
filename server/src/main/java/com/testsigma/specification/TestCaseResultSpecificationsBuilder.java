/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestCaseResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestCaseResultSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestCaseResult> result;

  public TestCaseResultSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestCaseResult> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestCaseResultSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestCaseResultSpecification(searchCriteria)));
    return result;
  }
}
