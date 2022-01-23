package com.testsigma.specification;

import com.testsigma.model.TestStep;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestStepSpecificationsBuilder extends BaseSpecificationsBuilder {

  public TestStepSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestStep> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new TestStepSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new TestStepSpecification(params.get(i)));
    }

    return result;
  }
}
