package com.testsigma.specification;

import com.testsigma.model.WorkspaceVersion;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class VersionSpecificationsBuilder extends BaseSpecificationsBuilder {

  public VersionSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<WorkspaceVersion> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new VersionSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new VersionSpecification(params.get(i)));
    }

    return result;
  }
}
