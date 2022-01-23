/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestCaseType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestCaseTypeSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestCaseType> result;

  public TestCaseTypeSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestCaseType> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestCaseTypeSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestCaseTypeSpecification(searchCriteria)));
    return result;
  }
}
