package com.testsigma.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


@AllArgsConstructor
@Data
public abstract class BaseSpecificationsBuilder {
  public List<SearchCriteria> params;

  public BaseSpecificationsBuilder with(String key, String operation, Object value, String prefix, String suffix) {

    SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
    if (op != null) {
      if (op == SearchOperation.EQUALITY) {
        boolean endWithAsterisk = prefix.contains("*");
        boolean startWithAsterisk = suffix.contains("*");

        if (startWithAsterisk && endWithAsterisk) {
          op = SearchOperation.CONTAINS;
        } else if (startWithAsterisk) {
          op = SearchOperation.STARTS_WITH;
        } else if (endWithAsterisk) {
          op = SearchOperation.ENDS_WITH;
        }
      } else if (op == SearchOperation.CONTAINS) {
        op = SearchOperation.CONTAINS;
      } else if (op == SearchOperation.NOT_IN)
        op = SearchOperation.NOT_IN;
      params.add(new SearchCriteria(key, op, value));
    }
    return this;
  }

  public abstract Specification<?> build();
}
