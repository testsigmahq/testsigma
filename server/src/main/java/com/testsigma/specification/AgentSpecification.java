package com.testsigma.specification;

import com.testsigma.model.Agent;

import javax.persistence.criteria.*;
import java.sql.Timestamp;

public class AgentSpecification extends BaseSpecification<Agent> {
  private static final long dayInMillis = 86400000L;
  private static final long TEN_MIN_IN_MILLISECONDS = 600000L;

  public AgentSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<Agent> root) {
    String key = criteria.getKey();
    if (key.equals("title")) {
      Join s = root.join("agent", JoinType.INNER);
      return s.get(key);
    } else if (key.equals("isActive")) {
      return root.get("updatedDate");
    } else {
      return root.get(criteria.getKey());
    }
  }

  @Override
  public Predicate toPredicate(Root<Agent> root,
                               CriteriaQuery<?> query,
                               CriteriaBuilder builder) {
    Predicate predicate = super.toPredicate(root, query, builder);

    if (criteria.getKey().equals("isActive")) {
      Timestamp yesterday = new Timestamp(System.currentTimeMillis() - TEN_MIN_IN_MILLISECONDS);
      if (criteria.getValue().equals("true"))
        return builder.greaterThan(root.get("updatedDate"), yesterday);
      else
        return builder.lessThan(root.get("updatedDate"), yesterday);
    }
    return predicate;
  }

}
