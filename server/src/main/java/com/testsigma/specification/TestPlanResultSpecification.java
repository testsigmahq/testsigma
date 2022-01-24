/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.google.common.primitives.Ints;
import com.testsigma.model.ExecutionTriggeredType;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestPlanResult;
import lombok.extern.log4j.Log4j2;

import javax.persistence.criteria.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Log4j2
public class TestPlanResultSpecification extends BaseSpecification<TestPlanResult> {

  public TestPlanResultSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "result":
        if (op == SearchOperation.IN) {
          List<ResultConstant> resultConstants = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            resultConstants.add(ResultConstant.valueOf(string));
          });
          return resultConstants;
        }
        return ResultConstant.valueOf(value.toString());
      case "status":
        if (op == SearchOperation.IN) {
          List<StatusConstant> statusConstants = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            statusConstants.add(StatusConstant.valueOf(string));
          });
          return statusConstants;
        }
        return StatusConstant.valueOf(value.toString());

      case "triggeredType":
        if (op == SearchOperation.IN) {
          List<ExecutionTriggeredType> executionTriggeredTypes = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            executionTriggeredTypes.add(ExecutionTriggeredType.valueOf(string));
          });
          return executionTriggeredTypes;
        }
        return ExecutionTriggeredType.valueOf(value.toString());
      default:
        return value;
    }
  }

  @Override
  public Predicate toPredicate(Root<TestPlanResult> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate;
    if (this.criteria.getKey().equals("term")) {
      predicate = builder.or(
        builder.like(root.get("buildNo"), "%" + criteria.getValue() + "%"),
        builder.equal(root.get("id"), Ints.tryParse(criteria.getValue().toString())));
    } else if (this.criteria.getKey().equals("startTime")) {
      parseStartTime(this.criteria);
      predicate = builder.greaterThan(getPath(criteria, root).as(Timestamp.class), (Timestamp) criteria.getValue());
    } else {
      predicate = super.toPredicate(root, query, builder);
    }
    return predicate;
  }

  private void parseStartTime(SearchCriteria criteria) {
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    // you can change format of date
    Date date = null;
    try {
      date = formatter.parse(criteria.getValue().toString());
    } catch (ParseException e) {
      log.error(e.getMessage(), e);
    }
    Timestamp timeStampDate = new Timestamp(date.getTime());
    criteria.setValue(timeStampDate);
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestPlanResult> root) {
    if (criteria.getKey().equals("workspaceVersionId")) {
      Join s = root.join("testPlan", JoinType.INNER);
      return s.get("workspaceVersionId");
    } else if (criteria.getKey().equals("entityType")) {
      Join testPlan = root.join("testPlan");
      return testPlan.get("entityType");
    }
    return root.get(criteria.getKey());
  }
}
