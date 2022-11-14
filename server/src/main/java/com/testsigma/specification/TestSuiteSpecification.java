package com.testsigma.specification;

import com.testsigma.model.ResultConstant;
import com.testsigma.model.StatusConstant;
import com.testsigma.model.TestSuite;
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
public class TestSuiteSpecification extends BaseSpecification<TestSuite> {

  public TestSuiteSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "lastRunOn":
      case "createdDate":
      case "updatedDate":
        return parseDate(value, op);
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
    } else if (criteria.getKey().equals("suiteId")) {
      return root.get("id");
    } else if (criteria.getKey().equals("createdBy")||criteria.getKey().equals("updatedBy")) {
      Join s = root.join(criteria.getKey(), JoinType.INNER);
      return s.get("firstName");
    } else if (criteria.getKey().equals("tagId")) {
      Join s = root.join("tagUses", JoinType.INNER);
      return s.get("tagId");
    } else if (criteria.getKey().equals("executionId")) {
      Join s = root.join("testDeviceSuites", JoinType.INNER);
      Join s1 = s.join("testDevice", JoinType.INNER);
      return s1.get("testPlanId");
    } else if (criteria.getKey().equals("environmentId")) {
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
    /*else if (criteria.getKey().equals("workspaceVersionId")) {
      Join s = root.join("environmentSuiteMappings", JoinType.INNER);
      Join s1 = s.join("testDevice", JoinType.INNER);
      return s1.get("applicationVersionId");
    }*/
    else if(criteria.getKey().equals("lastRunResult")){
      Join results = root.join("lastRun", JoinType.INNER);
      return results.get("result");
    } else if(criteria.getKey().equals("lastRunOn")) {
      Join results = root.join("lastRun", JoinType.INNER);
      return results.get("startTime");
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
