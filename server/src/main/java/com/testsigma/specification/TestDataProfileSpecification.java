package com.testsigma.specification;

import com.testsigma.model.TestCase;
import com.testsigma.model.TestData;

import javax.persistence.criteria.*;

public class TestDataProfileSpecification extends BaseSpecification<TestData> {
  public TestDataProfileSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Expression<String> getPath(SearchCriteria criteria, Root<TestData> root) {
    if (criteria.getKey().equals("versionId")) {
      return root.get("versionId");
    } else if (criteria.getKey().equals("isMapped")) {
      Join<TestData, TestCase> testDataTestCaseJoin = root.join("testCases", JoinType.LEFT);
      return testDataTestCaseJoin.get("testDataId");
    } else
      return super.getPath(criteria, root);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation operation) {
    return super.getEnumValueIfEnum(key, value, operation);
  }

  @Override
  public Predicate toPredicate(Root<TestData> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    if (criteria.getKey().equals("versionId")) {
      return super.toPredicate(root, query, builder);
    }
    if (criteria.getKey().equals("isMapped")) {
      Predicate predicate = null;
      if (criteria.getValue().toString().equals("used")) {
        query.where(getPath(criteria, root).isNotNull()).distinct(true);
        predicate = query.getRestriction();
      } else if (criteria.getValue().toString().equals("unused")) {
        query.where(getPath(criteria, root).isNull()).distinct(true);
        predicate = query.getRestriction();
      }
      return predicate;
    } else {
      return super.toPredicate(root, query, builder);
    }
  }
}
