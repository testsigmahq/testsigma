package com.testsigma.specification;

import com.testsigma.model.DryTestPlan;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class DryTestPlanSpecificationBuilder extends BaseSpecificationsBuilder {


  private Specification<DryTestPlan> result;

  public DryTestPlanSpecificationBuilder() {
    super(new ArrayList<>());
  }

  @Override
  public Specification<DryTestPlan> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new DryTestPlanSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new DryTestPlanSpecification(searchCriteria)));
    return result;
  }
}
