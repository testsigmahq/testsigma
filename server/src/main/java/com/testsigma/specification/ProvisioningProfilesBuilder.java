package com.testsigma.specification;

import com.testsigma.model.ProvisioningProfile;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ProvisioningProfilesBuilder extends BaseSpecificationsBuilder {

  public ProvisioningProfilesBuilder() {
    super(new ArrayList<>());
  }

  public Specification<ProvisioningProfile> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification<ProvisioningProfile> result = new ProvisioningProfileSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new ProvisioningProfileSpecification(params.get(i)));
    }

    return result;
  }
}
