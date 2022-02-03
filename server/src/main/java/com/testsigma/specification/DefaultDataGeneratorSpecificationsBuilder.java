package com.testsigma.specification;

import com.testsigma.model.DefaultDataGenerator;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class DefaultDataGeneratorSpecificationsBuilder extends BaseSpecificationsBuilder {

  public DefaultDataGeneratorSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<DefaultDataGenerator> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new DefaultDataGeneratorSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new DefaultDataGeneratorSpecification(params.get(i)));
    }

    return result;
  }
}
