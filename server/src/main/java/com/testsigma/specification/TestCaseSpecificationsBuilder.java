package com.testsigma.specification;

import com.fasterxml.jackson.databind.node.IntNode;
import com.testsigma.model.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TestCaseSpecificationsBuilder extends BaseSpecificationsBuilder {

  public TestCaseSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestCase> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new TestCaseSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new TestCaseSpecification(params.get(i)));
    }

    return result;
  }

  public Specification<TestCase> build(ListFilter filter, WorkspaceVersion version) {
    if (filter.getSearchCriteria().size() == 0) {
      return null;
    }

    List<SearchCriteria> criteriaList = filter.getSearchCriteria();
    Specification result = new TestCaseSpecification(criteriaList.get(0));
    for (SearchCriteria criteria : criteriaList) {
      normalizeAppVersion(criteria, version);
      normalizeEnums(criteria);
      normalizeDate(criteria);
      result = Specification.where(result).and(new TestCaseSpecification(criteria));
    }
    return result;
  }

  private void normalizeEnums(SearchCriteria criteria) {
    if (criteria.getKey().equals("status")) {
      Object[] values = ((ArrayList) criteria.getValue()).toArray();
      ArrayList<TestCaseStatus> normalizedList = new ArrayList<>();
      for (int index = 0; index < values.length; index++) {
        normalizedList.add(TestCaseStatus.valueOf(values[index].toString()));
      }
      criteria.setValue(normalizedList);
    }
    if (criteria.getKey().equals("result")) {
      Object[] values = ((ArrayList) criteria.getValue()).toArray();
      ArrayList<ResultConstant> normalizedList = new ArrayList<>();
      for (int index = 0; index < values.length; index++) {
        normalizedList.add(ResultConstant.valueOf(values[index].toString()));
      }
      criteria.setValue(normalizedList);
    }
  }

  private void normalizeAppVersion(SearchCriteria criteria, WorkspaceVersion version) {
    if (criteria.getKey().equals("workspaceVersionId") && criteria.getOperation().equals(SearchOperation.EQUALITY)) {
      if (criteria.getValue().toString().equals("-1")) {
        criteria.setValue(version.getId());
      } else {
        criteria.setValue(((IntNode) criteria.getValue()).asLong());
      }
    }
  }

  private void normalizeDate(SearchCriteria criteria) {
    if (criteria.getKey().equals("createdDate") || criteria.getKey().equals("updatedDate")) {
      criteria.setValue(criteria.getValue());
    }
  }
}
