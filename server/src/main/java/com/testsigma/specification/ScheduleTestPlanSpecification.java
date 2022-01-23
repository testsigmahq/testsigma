/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.testsigma.model.ScheduleStatus;
import com.testsigma.model.ScheduleTestPlan;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleTestPlanSpecification extends BaseSpecification<ScheduleTestPlan> {

  public ScheduleTestPlanSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "status":
        if (op == SearchOperation.IN) {
          if (value.getClass().getName().equals("java.lang.String")) {
            List<ScheduleStatus> resultConstants = new ArrayList<>();
            Arrays.asList(value.toString().split("#")).forEach(string -> {
              resultConstants.add(ScheduleStatus.valueOf(string));
            });
            return resultConstants;
          } else {
            return value;
          }
        }
        return ScheduleStatus.valueOf(value.toString());
      default:
        return super.getEnumValueIfEnum(key, value, op);
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<ScheduleTestPlan> root) {
    String key = criteria.getKey();
    if (key.equals("versionId")) {
      Join s = root.join("testPlan", JoinType.INNER);
      return s.get("workspaceVersionId");
    } else {
      return root.get(criteria.getKey());
    }
  }
}
