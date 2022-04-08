/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.specification;

import com.testsigma.model.UploadVersion;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class UploadVersionSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<UploadVersion> result;

  public UploadVersionSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<UploadVersion> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new UploadVersionSpecification(params.get(0));
    for (SearchCriteria searchCriteria : params) {
      if (!searchCriteria.getKey().equals("deviceId")) {
        result = Specification.where(result).and(new UploadVersionSpecification(searchCriteria));
      }
    }
    return result;
  }
}

