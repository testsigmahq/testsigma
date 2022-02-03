/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestPlanResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestPlanResultSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestPlanResult> result;

  public TestPlanResultSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestPlanResult> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestPlanResultSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestPlanResultSpecification(searchCriteria)));
    return result;
  }
}
