package com.testsigma.specification;

import com.testsigma.model.DefaultDataGenerator;

import javax.persistence.criteria.*;

public class DefaultDataGeneratorSpecification extends BaseSpecification<DefaultDataGenerator> {
  public DefaultDataGeneratorSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<DefaultDataGenerator> root) {
    String key = criteria.getKey();
    if (key.equals("term")) {
      return null;
    } else {
      return root.get(criteria.getKey());
    }
  }


  @Override
  public Predicate toPredicate(Root<DefaultDataGenerator> root,
                               CriteriaQuery<?> query,
                               CriteriaBuilder builder) {
    Predicate predicate = super.toPredicate(root, query, builder);
    if (this.criteria.getKey().equals("term")) {
      predicate = builder.or(
        builder.like(root.get("file").get("className"), "%" + criteria.getValue() + "%"),
        builder.like(root.get("functionName"), "%" + criteria.getValue() + "%"),
        builder.like(root.get("file").get("displayName"), "%" + criteria.getValue() + "%"));
    }
    return predicate;
  }
}
