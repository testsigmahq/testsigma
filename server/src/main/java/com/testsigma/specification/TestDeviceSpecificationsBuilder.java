/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestDevice;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class TestDeviceSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestDevice> result;

  public TestDeviceSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestDevice> build() {
    if (params.size() == 0) {
      return null;
    }


    params.forEach((searchCriteria) -> {
      if (searchCriteria.getKey().equals("appUploadId")) {
        String[] appIds = searchCriteria.getValue().toString().split("#");
        result = new TestDeviceSpecification(new SearchCriteria(searchCriteria.getKey(), searchCriteria.getOperation(), appIds[0]));
        for (String appId : appIds) {
          result = result.or(new TestDeviceSpecification(new SearchCriteria(searchCriteria.getKey(), searchCriteria.getOperation(), appId)));
        }
      } else {
        result = new TestDeviceSpecification(params.get(0));
        result = Specification.where(result).and(new TestDeviceSpecification(searchCriteria));
      }
    });
    return result;
  }
}
