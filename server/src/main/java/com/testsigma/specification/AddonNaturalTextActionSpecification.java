/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.AddonNaturalTextAction;
import com.testsigma.model.StepActionType;
import com.testsigma.model.WorkspaceType;
import com.testsigma.model.AddonStatus;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

public class AddonNaturalTextActionSpecification extends BaseSpecification<AddonNaturalTextAction> {

  public AddonNaturalTextActionSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "workspaceType":
        return WorkspaceType.valueOf(value.toString());
      case "deprecated":
        return Boolean.parseBoolean(value.toString());
      case "status":
        return AddonStatus.valueOf(value.toString());
      case "stepActionType":
        return StepActionType.valueOf(value.toString());
      default:
        return value;
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<AddonNaturalTextAction> root) {
    String key = criteria.getKey();
    if (key.equals("status")) {
      Join s = root.join("plugin", JoinType.INNER);
      return s.get(key);
    } else if (key.equals("createdBy")) {
      Join s = root.join("plugin", JoinType.INNER);
      return s.get(key);
    } else {
      return root.get(criteria.getKey());
    }
  }
}
