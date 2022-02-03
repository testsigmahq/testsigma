package com.testsigma.specification;

import com.testsigma.model.MobileInspection;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class MobileInspectionSpecificationBuilder extends BaseSpecificationsBuilder {
  private Specification<MobileInspection> result;

  public MobileInspectionSpecificationBuilder() {
    super(new ArrayList<>());
  }

  public Specification<MobileInspection> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new MobileInspectionSpecification(params.get(0));

    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new MobileInspectionSpecification(searchCriteria)));
    return result;
  }
}
