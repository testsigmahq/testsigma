package com.testsigma.specification;

import com.testsigma.model.AddonPluginTestDataFunction;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class AddonPluginTestDataFunctionSpecificationBuilder extends BaseSpecificationsBuilder {

  private Specification<AddonPluginTestDataFunction> result;

  public AddonPluginTestDataFunctionSpecificationBuilder() {
    super(new ArrayList<>());
  }

  public Specification<AddonPluginTestDataFunction> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new AddonPluginTestDataFunctionSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new AddonPluginTestDataFunctionSpecification(searchCriteria)));
    return result;
  }
}
