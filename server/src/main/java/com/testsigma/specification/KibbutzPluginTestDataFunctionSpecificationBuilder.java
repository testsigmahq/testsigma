package com.testsigma.specification;

import com.testsigma.model.KibbutzPluginTestDataFunction;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class KibbutzPluginTestDataFunctionSpecificationBuilder extends BaseSpecificationsBuilder {

  private Specification<KibbutzPluginTestDataFunction> result;

  public KibbutzPluginTestDataFunctionSpecificationBuilder() {
    super(new ArrayList<>());
  }

  public Specification<KibbutzPluginTestDataFunction> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new KibbutzPluginTestDataFunctionSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new KibbutzPluginTestDataFunctionSpecification(searchCriteria)));
    return result;
  }
}
