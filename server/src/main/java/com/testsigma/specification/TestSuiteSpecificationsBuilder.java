package com.testsigma.specification;

import com.testsigma.model.TestSuite;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestSuiteSpecificationsBuilder extends BaseSpecificationsBuilder {

  public TestSuiteSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestSuite> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new TestSuiteSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new TestSuiteSpecification(params.get(i)));
    }

    return result;
  }
}
