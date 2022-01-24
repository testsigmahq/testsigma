package com.testsigma.specification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testsigma.model.ResultConstant;
import com.testsigma.model.SuiteTestCaseMapping;
import com.testsigma.model.TestCase;
import com.testsigma.model.TestCaseStatus;
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
public class TestCaseSpecification extends BaseSpecification<TestCase> {

  public TestCaseSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "createdDate":
        return parseDate(value, op);
      case "updatedDate":
        return parseDate(value, op);
      case "status":
        if (op == SearchOperation.IN) {
          if (value.getClass().getName().equals("java.lang.String")) {
            List<TestCaseStatus> resultConstants = new ArrayList<>();
            Arrays.asList(value.toString().split("#")).forEach(string -> {
              resultConstants.add(TestCaseStatus.valueOf(string));
            });
            return resultConstants;
          } else {
            return value;
          }
        }
        return TestCaseStatus.valueOf(value.toString());
      case "result":
        if (op == SearchOperation.IN) {
          if (value.getClass().getName().equals("java.lang.String")) {
            List<ResultConstant> resultConstants = new ArrayList<>();
            Arrays.asList(value.toString().split("#")).forEach(string -> {
              if (string.equals("null"))
                resultConstants.add(null);
              else
                resultConstants.add(ResultConstant.valueOf(string));
            });
            return resultConstants;
          } else {
            return value;
          }
        }
        return ResultConstant.valueOf(value.toString());
      case "isStepGroup":
      case "deleted":
        return Boolean.parseBoolean(value.toString());
      default:
        return super.getEnumValueIfEnum(key, value, op);
    }
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestCase> root) {
    if (criteria.getKey().equals("tagId")) {
      Join s = root.join("tagUses", JoinType.INNER);
      return s.get("tagId");
    } else if (criteria.getKey().equals("stepGroupId")) {
      Join s = root.join("testSteps", JoinType.INNER);
      return s.get("stepGroupId");
    } else if (criteria.getKey().equals("suiteId")) {
      Join s = root.join("suiteTestCaseMappings", JoinType.INNER);
      return s.get("suiteId");
    } else if (criteria.getKey().equals("result")) {
      Join s = root.join("lastRun", JoinType.LEFT);
      return s.get("result");
    } else if (criteria.getKey().equals("element")) {
      Join s = root.join("testSteps", JoinType.INNER);
      return s.get("element");
    } else if (criteria.getKey().equals("customStep")) {
      Join s = root.join("testSteps", JoinType.INNER);
      return s.get("dataMap");
    } else if (criteria.getKey().equals("caseId"))
      return root.get("id");
    return root.get(criteria.getKey());
  }

  @Override
  public Predicate toPredicate(Root<TestCase> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    String key = this.criteria.getKey();
    if (key.equals("suiteId")) {
      Predicate predicate = super.toPredicate(root, query, builder);
      for (Join<?, ?> join : root.getJoins()) {
        if (join.getAttribute().getName().equals("suiteTestCaseMappings"))
          query.orderBy(builder.asc(join.get("position")));
      }
      return predicate;
    }
    if (key.equals("suiteMapping")) {
      Predicate predicate = null;
      if (criteria.getValue().toString().equals("true")) {
        Join<TestCase, SuiteTestCaseMapping> testCaseSuiteMappingJoin = root.join("suiteTestCaseMappings", JoinType.LEFT);
        query.where(builder.isNotNull(testCaseSuiteMappingJoin.get("testCaseId"))).distinct(true);
        predicate = query.getRestriction();
      } else if (criteria.getValue().toString().equals("false")) {
        Join<TestCase, SuiteTestCaseMapping> testCaseSuiteMappingJoin = root.join("suiteTestCaseMappings", JoinType.LEFT);
        query.where(builder.isNull(testCaseSuiteMappingJoin.get("testCaseId"))).distinct(true);
        predicate = query.getRestriction();
      }
      return predicate;
    } else if (key.equals("customStep")) {
      super.toPredicate(root, query, builder);
      query.groupBy(root.get("id"));
      return handleCustomFunctionMultiValue(criteria, query, builder, root);
    } else if (key.equals("element")) {
      super.toPredicate(root, query, builder);
      query.groupBy(root.get("id"));
      return builder.equal(
        getPath(criteria, root),
        criteria.getValue());
    } else if (key.startsWith("cf_")) {
      String cf_key = this.criteria.getKey();
      cf_key = cf_key.replaceAll("_ts_q_space_", " ").replaceAll("cf_", "");
      if (criteria.getOperation().equals(SearchOperation.IN)) {
        return this.handleCustomFieldMultiValue(cf_key, criteria, builder, root);
      } else {
        return builder.equal(
          builder.function("JSON_EXTRACT", String.class, getPath(criteria, root), builder.literal("$.\"" + cf_key + "\"")),
          criteria.getValue().toString());
      }
    } else if (key.equals("stepGroupId") || key.equals("tagId")) {
      query.groupBy(root.get("id"));
      return super.toPredicate(root, query, builder);
    } else {
      return super.toPredicate(root, query, builder);
    }
  }

  private Predicate handleCustomFieldMultiValue(String key, SearchCriteria criteria, CriteriaBuilder builder, Root<TestCase> root) {
    String[] values;
    if (criteria.getValue() instanceof ArrayList)
      values = (String[]) ((ArrayList) criteria.getValue()).toArray(new String[((ArrayList) criteria.getValue()).size()]);
    else if (criteria.getValue() instanceof String[])
      values = (String[]) criteria.getValue();
    else
      values = ((String) criteria.getValue()).split("#");
    criteria.setValue(values);
    try {
      String vaa = new ObjectMapper().writeValueAsString(values);
      Predicate predicate = builder.equal(
        builder.function(
          "JSON_CONTAINS",
          Boolean.class,
          builder.function("JSON_EXTRACT", String[].class, getPath(criteria, root), builder.literal("$.\"" + key + "\"")),
          builder.literal(vaa)
        ),
        true
      );
      String singleValue = null;
      for (String value : values) {
        singleValue = value;
      }
      return builder.or(builder.equal(
        builder.function("JSON_EXTRACT", String.class, getPath(criteria, root), builder.literal("$.\"" + key + "\"")),
        builder.literal(singleValue)
      ), predicate);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
  }

  private Predicate handleCustomFunctionMultiValue(SearchCriteria criteria, CriteriaQuery<?> query,
                                                   CriteriaBuilder builder, Root<TestCase> root) {
    String[] values;
    if (criteria.getValue() instanceof ArrayList)
      values = (String[]) ((ArrayList) criteria.getValue()).toArray(new String[((ArrayList) criteria.getValue()).size()]);
    else if (criteria.getValue() instanceof String[])
      values = (String[]) criteria.getValue();
    else
      values = ((String) criteria.getValue()).split("#");
    criteria.setValue(values);
    query.groupBy(root.get("id"));
    Expression<String> expression = getPath(criteria, root);
    try {
      Predicate[] predicates = new Predicate[values.length];
      for (int i = 0; i < values.length; i++) {
        predicates[i] = (builder.equal(
          builder.function("JSON_EXTRACT", Long.class, expression, builder.literal("$.\"custom-step\".id")),
          values[i]));
      }
      return builder.or(predicates);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return null;
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
