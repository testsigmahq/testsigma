/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.Requirement;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class RequirementSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<Requirement> result;

  public RequirementSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Requirement> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new RequirementSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new RequirementSpecification(searchCriteria)));
    return result;
  }
}
