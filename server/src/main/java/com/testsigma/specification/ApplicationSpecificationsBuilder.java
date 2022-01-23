package com.testsigma.specification;

import com.testsigma.model.Workspace;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ApplicationSpecificationsBuilder extends BaseSpecificationsBuilder {

  public ApplicationSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Workspace> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new ApplicationSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new ApplicationSpecification(params.get(i)));
    }

    return result;
  }
}
