package com.testsigma.specification;

import com.testsigma.model.WorkspaceVersion;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

public class VersionSpecification extends BaseSpecification<WorkspaceVersion> {

  public VersionSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    if ("isDemo".equals(key)) {
      return Boolean.parseBoolean(value.toString());
    }
    return value;
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<WorkspaceVersion> root) {
    if (criteria.getKey().equals("isDemo")) {
      Join s = root.join("workspace", JoinType.INNER);
      return s.get("isDemo");
    }
    return root.get(criteria.getKey());
  }
}
