package com.testsigma.specification;

import com.testsigma.model.MobileInspection;
import com.testsigma.model.MobileInspectionStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MobileInspectionSpecification extends BaseSpecification<MobileInspection> {
  public MobileInspectionSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "status":
        if (op == SearchOperation.IN) {
          List<MobileInspectionStatus> mobileInspectionStatuses = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            mobileInspectionStatuses.add(MobileInspectionStatus.valueOf(string));
          });
          return mobileInspectionStatuses;
        }
        return MobileInspectionStatus.valueOf(value.toString());
      default:
        return value;
    }
  }
}
