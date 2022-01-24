/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.specification;

import com.testsigma.model.Upload;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class UploadSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<Upload> result;

  public UploadSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Upload> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new UploadSpecification(params.get(0));
    for (SearchCriteria searchCriteria : params) {
      if (!searchCriteria.getKey().equals("deviceId")) {
        result = Specification.where(result).and(new UploadSpecification(searchCriteria));
      }
    }
    return result;
  }
}

