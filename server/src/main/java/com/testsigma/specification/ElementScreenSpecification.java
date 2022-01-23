/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.specification;

import com.testsigma.model.ElementScreenName;
import lombok.extern.log4j.Log4j2;

import javax.persistence.criteria.*;

@Log4j2
public class ElementScreenSpecification extends BaseSpecification<ElementScreenName> {
  public ElementScreenSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    return value;
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<ElementScreenName> root) {
    return root.get(criteria.getKey());
  }

  @Override
  public Predicate toPredicate(Root<ElementScreenName> root,
                               CriteriaQuery<?> query,
                               CriteriaBuilder builder) {
    if (criteria.getKey().equals("name"))
      return builder.like(getPath(criteria, root), "%" + criteria.getValue().toString() + "%");
    else
      return super.toPredicate(root, query, builder);
  }
}
