package com.testsigma.specification;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author PratheepV
 */
@Data
@AllArgsConstructor
public class SearchCriteria {
  private String key;
  private SearchOperation operation;
  private Object value;
}
