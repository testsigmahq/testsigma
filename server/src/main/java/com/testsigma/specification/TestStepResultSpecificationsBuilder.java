/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestStepResult;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestStepResultSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestStepResult> result;

  public TestStepResultSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestStepResult> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestStepResultSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestStepResultSpecification(searchCriteria)));
    return result;
  }
}
