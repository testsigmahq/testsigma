package com.testsigma.specification;

import com.testsigma.model.TestStep;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

public class TestStepSpecification extends BaseSpecification<TestStep> {
  public TestStepSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestStep> root) {
    if (criteria.getKey().equals("workspaceVersionId")) {
      Join s = root.join("testCase", JoinType.INNER);
      return s.get("workspaceVersionId");
    }
    return root.get(criteria.getKey());
  }
}
