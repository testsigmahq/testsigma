/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.*;
import lombok.extern.log4j.Log4j2;

import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
@Log4j2
public class TestPlanSpecification extends BaseSpecification<TestPlan> {

  public TestPlanSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "lastRunOn":
      case "createdDate":
      case "updatedDate":
        return parseDate(value, op);
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
      case "result":
      case "lastRunResult":
        if (op == SearchOperation.IN) {
          List<ResultConstant> resultConstants = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            resultConstants.add(ResultConstant.valueOf(string));
          });
          return resultConstants;
        }
        return ResultConstant.valueOf(value.toString());
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
    } else if (criteria.getKey().equals("tagId")) {
      Join s = root.join("tagUses", JoinType.INNER);
      return s.get("tagId");
    } else if (criteria.getKey().equals("agentId")) {
      Join environments = root.join("testDevices", JoinType.INNER);
      return environments.get("agentId");
    } else if(criteria.getKey().equals("testPlanLabType")){
      Join environments = root.join("testDevices", JoinType.INNER);
      return environments.get("testPlanLabType");
    } else if(criteria.getKey().equals("lastRunOn")){
      Join results = root.join("lastRun", JoinType.INNER);
      return results.get("startTime");
    } else if(criteria.getKey().equals("lastRunResult")){
      Join results = root.join("lastRun", JoinType.INNER);
      return results.get("result");
    } else if (criteria.getKey().equals("createdBy")) // TO support old filters if any having createdBy as filter key
      return root.get("createdById");
    else if (criteria.getKey().equals("updatedBy")) // TO support old filters if any having updatedBy as filter key
      return root.get("updatedById");
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
  protected Object parseDate(Object value, SearchOperation op) {
    String valueStr = value.toString();
    if (op.equals(SearchOperation.LESS_THAN))
      valueStr = valueStr + " 23:59:59";
    if (op.equals(SearchOperation.GREATER_THAN))
      valueStr = valueStr + " 00:00:00";
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    try {
      return format.parse(valueStr);
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
      return null;
    }
  }
}
