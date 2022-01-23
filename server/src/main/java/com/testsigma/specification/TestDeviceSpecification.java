/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestDevice;

import javax.persistence.criteria.*;

public class TestDeviceSpecification extends BaseSpecification<TestDevice> {
  public TestDeviceSpecification(final SearchCriteria criteria) {
    super(criteria);
  }


  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestDevice> root) {
    return root.get(criteria.getKey());
  }

  public Predicate toPredicate(Root<TestDevice> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    return super.toPredicate(root, query, builder);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    if (key.equals("disable"))
      return Boolean.parseBoolean(value.toString());
    else
      return value;
  }
}
