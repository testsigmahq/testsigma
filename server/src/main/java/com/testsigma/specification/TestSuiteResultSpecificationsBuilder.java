/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestSuiteResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestSuiteResultSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestSuiteResult> result;

  public TestSuiteResultSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestSuiteResult> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestSuiteResultSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestSuiteResultSpecification(searchCriteria)));
    return result;
  }
}
