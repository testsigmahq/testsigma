package com.testsigma.specification;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestSuite;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestSuiteSpecification extends BaseSpecification<TestSuite> {

  public TestSuiteSpecification(final SearchCriteria criteria) {
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
      case "hasDataDrivenCases":
        return Boolean.parseBoolean(value.toString());
      default:
        return super.getEnumValueIfEnum(key, value, op);
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestSuite> root) {
    if (criteria.getKey().equals("testcaseId")) {
      Join s = root.join("testSuiteMappings", JoinType.INNER);
      Join s1 = s.join("testCase", JoinType.INNER);
      return s1.get("id");
    } else if (criteria.getKey().equals("tagId")) {
      Join s = root.join("tagUses", JoinType.INNER);
      return s.get("tagId");
    } else if (criteria.getKey().equals("testPlanId")) {
      Join s = root.join("testDeviceSuites", JoinType.INNER);
      Join s1 = s.join("testDevice", JoinType.INNER);
      return s1.get("testPlanId");
    } else if (criteria.getKey().equals("testDeviceId")) {
      Join s = root.join("testDeviceSuites", JoinType.INNER);
      return s.get("testDeviceId");
    } else if (criteria.getKey().equals("testCaseId") || criteria.getKey().equals("hasDataDrivenCases")) {
      Join s = root.join("testSuiteMappings", JoinType.INNER);
      if(criteria.getKey().equals("hasDataDrivenCases")) {
        Join r = s.join("testCase", JoinType.INNER);
        return r.get("isDataDriven");
      }
      else {
        return s.get("testCaseId");
      }
    }
    return root.get(criteria.getKey());
  }

  @Override
  public Predicate toPredicate(Root<TestSuite> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Predicate predicate = super.toPredicate(root, query, builder);
    Join environmentSuiteMappingJoin = null;
    if (criteria.getKey().equals("testPlanId") || criteria.getKey().equals("testPlanId")) {
      for (Join<?, ?> join : root.getJoins()) {
        if (join.getAttribute().getName().equals("testDeviceSuites"))
          environmentSuiteMappingJoin = join;
        query.orderBy(builder.asc(join.get("position")));
      }
      if (environmentSuiteMappingJoin != null)
        query.groupBy(root.get("id"), environmentSuiteMappingJoin.get("position"));
      else
        query.groupBy(root.get("id"));
    }
    return predicate;
  }
}
