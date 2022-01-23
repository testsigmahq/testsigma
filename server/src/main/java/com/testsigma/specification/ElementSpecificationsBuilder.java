/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.specification;

import com.fasterxml.jackson.databind.node.TextNode;
import com.testsigma.model.WorkspaceVersion;
import com.testsigma.model.Element;
import com.testsigma.model.ElementFilter;
import com.testsigma.model.LocatorType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ElementSpecificationsBuilder extends BaseSpecificationsBuilder {

  private Specification<Element> result;

  public ElementSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<Element> build() {
    if (params.size() == 0) {
      return null;
    }

    result = new ElementSpecification(params.get(0));
    params.forEach((searchCriteria) -> result =
      Specification.where(result).and(new ElementSpecification(searchCriteria)));
    return result;
  }

  public Specification<Element> build(ElementFilter elementFilter, WorkspaceVersion version) {
    if (elementFilter.getSearchCriteria().size() == 0) {
      return null;
    }

    List<SearchCriteria> criteriaList = elementFilter.getSearchCriteria();
    result = new ElementSpecification(criteriaList.get(0));
    for (SearchCriteria criteria : criteriaList) {
      normalizeDate(criteria);
      normalizeAppVersion(criteria, version);
      normalizeOperation(criteria);
      normalizeEnums(criteria);
      result = Specification.where(result).and(new ElementSpecification(criteria));
    }
    return result;
  }

  private void normalizeDate(SearchCriteria criteria) {
    if (criteria.getKey().equals("createdDate") || criteria.getKey().equals("updatedDate")) {
      criteria.setValue(criteria.getValue());
    }
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
    if (criteria.getKey().equals("locatorType")) {
      Object[] values = ((ArrayList) criteria.getValue()).toArray();
      ArrayList<LocatorType> normalizedList = new ArrayList<>();
      for (int index = 0; index < values.length; index++) {
        normalizedList.add(LocatorType.valueOf(values[index].toString()));
      }
      criteria.setValue(normalizedList);
    }
  }

  private void normalizeAppVersion(SearchCriteria criteria, WorkspaceVersion version) {
    if (criteria.getKey().equals("workspaceVersionId") && criteria.getOperation().equals(SearchOperation.EQUALITY)) {
      if (criteria.getValue().toString().equals("-1")) {
        criteria.setValue(version.getId());
      } else {
        criteria.setValue(Long.parseLong((String) criteria.getValue()));
      }
    }
  }
}
