/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.WorkspaceType;
import com.testsigma.model.AddonStatus;
import com.testsigma.model.KibbutzPluginTestDataFunction;

import javax.persistence.criteria.*;

public class KibbutzPluginTestDataFunctionSpecification extends BaseSpecification<KibbutzPluginTestDataFunction> {

  public KibbutzPluginTestDataFunctionSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "applicationType":
        return WorkspaceType.valueOf(value.toString());
      case "deprecated":
        return Boolean.parseBoolean(value.toString());
      case "status":
        return AddonStatus.valueOf(value.toString());
      default:
        return value;
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<KibbutzPluginTestDataFunction> root) {
    String key = criteria.getKey();
    if (key.equals("status")) {
      Join s = root.join("plugin", JoinType.INNER);
      return s.get(key);
    }
    else if (key.equals("createdBy")) {
      Join s = root.join("plugin", JoinType.INNER);
      return s.get(key);
    }
    else{
      return root.get(criteria.getKey());
    }
  }

  @Override
  public Predicate toPredicate(Root<KibbutzPluginTestDataFunction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate;
    if(criteria.getKey().equals("status")){
      Join s = root.join("plugin", JoinType.INNER);
      Object value = getEnumValueIfEnum(criteria.getKey(), criteria.getValue(), criteria.getOperation());
      predicate = builder.equal(s.get("status"), value);
      predicate = builder.or(predicate,
        builder.and(
          builder.equal(s.get("status"), AddonStatus.DRAFT)
        ));
      return predicate;
    } else
      predicate = super.toPredicate(root, query, builder);
    return  predicate;
  }
}
