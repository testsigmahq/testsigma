/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

package com.testsigma.specification;

import com.fasterxml.jackson.databind.node.TextNode;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.TestData;
import com.testsigma.model.TestDataFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TestDataProfileSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<TestData> result;

  public TestDataProfileSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<TestData> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new TestDataProfileSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new TestDataProfileSpecification(searchCriteria)));
    return result;
  }

  public Specification<TestData> build(TestDataFilter testDataFilter, WorkspaceVersion version) {
    if (testDataFilter.getSearchCriteria().size() == 0) {
      return null;
    }

    List<SearchCriteria> criteriaList = testDataFilter.getSearchCriteria();
    result = new TestDataProfileSpecification(criteriaList.get(0));
    for (SearchCriteria criteria : criteriaList) {
      normalizeAppVersion(criteria, version);
      normalizeOperation(criteria);
      normalizeEnums(criteria);
      result = Specification.where(result).and(new TestDataProfileSpecification(criteria));
    }
    return result;
  }

  private void normalizeOperation(SearchCriteria criteria) {
    if (criteria.getValue().getClass().equals(TextNode.class)) {
      if (((TextNode) criteria.getValue()).asText().endsWith("*") && ((TextNode) criteria.getValue()).asText().startsWith("*")) {
        criteria.setOperation(SearchOperation.CONTAINS);
        criteria.setValue(((TextNode) criteria.getValue()).asText().substring(1, ((TextNode) criteria.getValue()).asText().length() - 1));
      }
    } else {
      if (criteria.getValue().toString().endsWith("*") && criteria.getValue().toString().startsWith("*")) {
        criteria.setOperation(SearchOperation.CONTAINS);
        criteria.setValue(criteria.getValue().toString().substring(1, criteria.getValue().toString().length() - 1));
      }
    }
  }

  private void normalizeEnums(SearchCriteria criteria) {
  }

  private void normalizeAppVersion(SearchCriteria criteria, WorkspaceVersion version) {
    if (criteria.getKey().equals("workspaceVersionId") && criteria.getOperation().equals(SearchOperation.EQUALITY)) {
      if (criteria.getValue().toString().equals("-1")) {
        criteria.setValue(version.getId());
      } else {
        criteria.setValue(Long.parseLong(((TextNode) criteria.getValue()).asText()));
      }
    }
  }

}
