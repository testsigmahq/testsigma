package com.testsigma.specification;

import com.testsigma.model.Environment;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class EnvironmentSpecificationsBuilder extends BaseSpecificationsBuilder {

  public EnvironmentSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Environment> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new EnvironmentSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new VersionSpecification(params.get(i)));
    }

    return result;
  }
}
