package com.testsigma.specification;

import com.testsigma.model.SuiteTestCaseMapping;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class SuiteTestCaseMappingSpecificationsBuilder extends BaseSpecificationsBuilder {

  public SuiteTestCaseMappingSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<SuiteTestCaseMapping> build() {

    if (params.size() == 0) {
      return null;
    }

    Specification result = new SuiteTestCaseSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new SuiteTestCaseSpecification(params.get(i)));
    }

    return result;
  }
}
