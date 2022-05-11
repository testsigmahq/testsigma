package com.testsigma.specification;

import com.testsigma.model.RestStep;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class RestStepSpecificationsBuilder extends BaseSpecificationsBuilder {

  public RestStepSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<RestStep> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new RestStepSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new RestStepSpecification(params.get(i)));
    }

    return result;
  }
}
