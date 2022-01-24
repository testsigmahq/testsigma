package com.testsigma.specification;

import com.testsigma.model.Attachment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AttachmentSpecification extends BaseSpecification<Attachment> {

  public AttachmentSpecification(final SearchCriteria criteria) {
    super(criteria);
  }

  @Override
  protected Object getEnumValueIfEnum(String key, Object value, SearchOperation op) {
    switch (key) {
      case "entity":
        if (op == SearchOperation.IN) {
          List<String> permissionEntities = new ArrayList<>();
          Arrays.asList(value.toString().split("#")).forEach(string -> {
            permissionEntities.add(string);
          });
          return permissionEntities;
        }
        return value.toString();
      default:
        return value;
    }
  }
}
