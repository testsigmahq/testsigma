package com.testsigma.specification;

import com.testsigma.model.StepResultScreenshotComparison;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;

public class ScreenshotComparisionSpecificationsBuilder extends BaseSpecificationsBuilder {

  public ScreenshotComparisionSpecificationsBuilder() {
    super(new ArrayList<>());
  }

  public Specification<StepResultScreenshotComparison> build() {
    if (params.size() == 0) {
      return null;
    }

    Specification result = new ScreenshotComparisionSpecification(params.get(0));

    for (int i = 1; i < params.size(); i++) {
      result = Specification.where(result).and(new ScreenshotComparisionSpecification(params.get(i)));
    }

    return result;
  }
}
