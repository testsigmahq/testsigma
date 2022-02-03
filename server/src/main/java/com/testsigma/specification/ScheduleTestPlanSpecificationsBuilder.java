/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.ScheduleTestPlan;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ScheduleTestPlanSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<ScheduleTestPlan> result;

  public ScheduleTestPlanSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<ScheduleTestPlan> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new ScheduleTestPlanSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new ScheduleTestPlanSpecification(searchCriteria)));
    return result;
  }
}
