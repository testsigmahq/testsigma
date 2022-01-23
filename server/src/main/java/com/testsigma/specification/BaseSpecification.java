package com.testsigma.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public abstract class BaseSpecification<M> implements Specification<M> {

  protected SearchCriteria criteria;

  public BaseSpecification(final SearchCriteria criteria) {
    super();
    this.criteria = criteria;
  }

  @Override
  public Predicate toPredicate(Root<M> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Object value = getEnumValueIfEnum(criteria.getKey(), criteria.getValue(), criteria.getOperation());
    switch (criteria.getOperation()) {
      case EQUALITY:
        return "null".equals(value.toString()) ? builder.isNull(getPath(criteria, root)) :
          builder.equal(getPath(criteria, root), value);
      case NEGATION:
        return "null".equals(value.toString()) ? builder.isNotNull(getPath(criteria, root)) :
          builder.notEqual(getPath(criteria, root), value);
      case GREATER_THAN:
        return value instanceof Date ? builder.greaterThan(getPath(criteria, root).as(Date.class), (Date) value) :
          builder.greaterThan(getPath(criteria, root), criteria.getValue().toString());
      case LESS_THAN:
        return value instanceof Date ? builder.lessThan(getPath(criteria, root).as(Date.class), (Date) value) :
          builder.lessThan(getPath(criteria, root), criteria.getValue().toString());
      case LIKE:
        return builder.like(getPath(criteria, root), criteria.getValue().toString());
      case STARTS_WITH:
        return builder.like(getPath(criteria, root), criteria.getValue() + "%");
      case ENDS_WITH:
        return builder.like(getPath(criteria, root), "%" + criteria.getValue());
      case CONTAINS:
        return builder.like(builder.lower(getPath(criteria, root)), "%" + criteria.getValue().toString().toLowerCase() + "%");
      case IN:
        Expression<String> inExpression = getPath(criteria, root);
        if (value instanceof String)
          return inExpression.in(Arrays.asList(criteria.getValue().toString().split("#")));
        else {
          if (((ArrayList) value).contains(null)) {
            Predicate predicate = builder.or(builder.isNull(getPath(criteria, root)));
            return builder.or(inExpression.in((ArrayList) value), predicate);
          } else {
            return inExpression.in((ArrayList) value);
          }
        }
      case NOT_IN:
        inExpression = getPath(criteria, root);
        if (value instanceof String)
          return inExpression.in(Arrays.asList(criteria.getValue().toString().split("#"))).not();
        else {
          if (((ArrayList) value).contains(null)) {
            Predicate predicate = builder.or(builder.isNull(getPath(criteria, root)));
            return builder.or(inExpression.in((ArrayList) value), predicate);
          } else {
            return inExpression.in((ArrayList) value).not();
          }
        }
      default:
        return null;
    }
  }

  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation operation) {
    if (key.equals("createdDate") || key.equals("updatedDate")) {
      return new Date(Long.parseLong((String) criteria.getValue()));
    }
    return value;
  }

  protected Expression<String> getPath(SearchCriteria criteria, Root<M> root) {
    return root.get(criteria.getKey());
  }
}
