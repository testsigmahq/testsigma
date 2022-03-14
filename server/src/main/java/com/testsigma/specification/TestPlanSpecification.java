/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.TestPlanType;
import com.testsigma.model.TestPlan;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPlanSpecification extends BaseSpecification<TestPlan> {

  public TestPlanSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "testPlanType":
        if (op == SearchOperation.IN) {
          if (value.getClass().getName().equals("java.lang.String")) {
            List<TestPlanType> resultConstants = new ArrayList<>();
            Arrays.asList(value.toString().split("#")).forEach(string -> {
              resultConstants.add(TestPlanType.valueOf(string));
            });
            return resultConstants;
          } else {
            return value;
          }
        }
        return TestPlanType.valueOf(value.toString());
      case "testPlanLabType":
        if (op == SearchOperation.IN) {
          if (value.getClass().getName().equals("java.lang.String")) {
            List<TestPlanLabType> resultConstants = new ArrayList<>();
            Arrays.asList(value.toString().split("#")).forEach(string -> {
              resultConstants.add(TestPlanLabType.valueOf(string));
            });
            return resultConstants;
          } else {
            return value;
          }
        }
        return TestPlanLabType.valueOf(value.toString());
      default:
        return super.getEnumValueIfEnum(key, value, op);
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestPlan> root) {
    if (criteria.getKey().equals("suiteId")) {
      Join environments = root.join("testDevices", JoinType.INNER);
      Join envSuites = environments.join("environmentSuites", JoinType.INNER);
      return envSuites.get("suiteId");
    } else if (criteria.getKey().equals("agentId")) {
      Join environments = root.join("testDevices", JoinType.INNER);
      return environments.get("agentId");
    }
    return root.get(criteria.getKey());
  }

  @Override
  public Predicate toPredicate(Root<TestPlan> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate = super.toPredicate(root, query, builder);
    if (criteria.getKey().equals("suiteId")) {
      for (Join<?, ?> join : root.getJoins()) {
        if (join.getAttribute().getName().equals("testDevices"))
          query.groupBy(root.get("id"));
      }
    }
    return predicate;
  }
}
