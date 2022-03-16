/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
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
public class ElementSpecification extends BaseSpecification<Element> {
  static long workspaceVersionId = -1;

  public ElementSpecification(final SearchCriteria criteria) {
    super(criteria);
    if (criteria.getKey().equals("workspaceVersionId") && criteria.getValue().getClass().equals(Long.class))
      workspaceVersionId = (Long) criteria.getValue();
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "createdDate":
        return parseDate(value, op);
      case "updatedDate":
        return parseDate(value, op);
      case "createdType":
        if (op == SearchOperation.IN) {
          List<ElementCreateType> resultStatuses = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            resultStatuses.add(ElementCreateType.valueOf(string));
          });
          return resultStatuses;
        }
        return ElementCreateType.valueOf(value.toString());
      case "locatorType":
        if (op == SearchOperation.IN) {
          if (value.getClass().getName().equals("java.lang.String")) {
            List<LocatorType> resultStatuses = new ArrayList<>();
            Arrays.asList(value.toString().split("#")).forEach(string -> {
              resultStatuses.add(LocatorType.valueOf(string));
            });
            return resultStatuses;
          } else {
            return value;
          }
        }
        return LocatorType.valueOf(value.toString());
      case "isDuplicated":
        return Boolean.parseBoolean(value.toString());
      default:
        return value;
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<Element> root) {
    if (criteria.getKey().equals("tagId")) {
      Join s = root.join("tagUses", JoinType.INNER);
      return s.get("tagId");
    }
    if (criteria.getKey().equals("isUsed")) {
      return root.get("name");
    }
    if (criteria.getKey().equals("screenName")) {
      Join s = root.join("screenNameObj", JoinType.INNER);
      return s.get("name");
    }
    return root.get(criteria.getKey());
  }

  @Override
  public Predicate toPredicate(Root<Element> root,
                               CriteriaQuery<?> query,
                               CriteriaBuilder builder) {
    Predicate predicate = super.toPredicate(root, query, builder);
    if (criteria.getKey().equals("tagId")) {
      query.groupBy(root.get("id"));
    }
    if (criteria.getKey().equals("isUsed")) {
      if (criteria.getValue().toString().equals("false")) {
        Subquery<String> subQuery = query.subquery(String.class);
        Root<TestStep> subStepRoot = subQuery.from(TestStep.class);

        Expression<String> uiidNamefromTestStepExpr = subStepRoot.get("element");
        subQuery.select(uiidNamefromTestStepExpr).where(builder.isNotNull(uiidNamefromTestStepExpr));
        query.where(root.get("name").in(subQuery).not());
        predicate = query.getRestriction();
      }
      if (criteria.getValue().toString().equals("true")) {
        //TODO Optimize query if performance issues arises
        Subquery<String> subQuery = query.subquery(String.class);
        Root<TestStep> subStepRoot = subQuery.from(TestStep.class);
        Join<TestStep, TestCase> testStepTestCaseJoin = subStepRoot.join("testCase", JoinType.LEFT);
        Expression<String> uiidNamefromTestStepExpr = subStepRoot.get("element");

        subQuery.select(uiidNamefromTestStepExpr).where(
          builder.isNotNull(uiidNamefromTestStepExpr),
          builder.equal(testStepTestCaseJoin.get("deleted"), true).not(),
          builder.equal(testStepTestCaseJoin.get("workspaceVersionId").as(Long.class), workspaceVersionId)
        );
        query.where(root.get("name").in(subQuery));
        predicate = query.getRestriction();
      }
    }
    return predicate;
  }

  private Object parseDate(Object value, SearchOperation op) {
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
