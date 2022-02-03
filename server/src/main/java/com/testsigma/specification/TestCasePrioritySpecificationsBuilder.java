/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestCasePriority;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestCasePrioritySpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestCasePriority> result;

  public TestCasePrioritySpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestCasePriority> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestCasePrioritySpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestCasePrioritySpecification(searchCriteria)));
    return result;
  }
}
