package com.testsigma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum LocatorType {
  xpath(1, "xpath"),
  csspath(2, "css selector"),
  id_value(3, "id"),
  name(4, "name"),
  link_text(5, "link text"),
  partial_link_text(6, "partial link text"),
  class_name(7, "class name"),
  tag_name(8, "tag name"),
  accessibility_id(9, "accessibility id");

  private final Integer id;
  private final String displayName;


  public static Map<String, Integer> getIdVsDisplayNameMap() {
    Map<String, Integer> toReturn = new HashMap<>();
    for (LocatorType type : LocatorType.values()) {
      if (!type.getDisplayName().equals("Not Used")) {
        toReturn.put(type.getDisplayName().toLowerCase(), type.getId());
      }
    }
    return toReturn;
  }

  public static LocatorType getLocatorTypeById(Integer id) {
    for (LocatorType type : LocatorType.values()) {
      if (type.getId().equals(id)) {
        return type;
      }
    }
    return null;
  }
}
