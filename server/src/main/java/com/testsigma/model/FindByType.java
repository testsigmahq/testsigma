package com.testsigma.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum FindByType {
//  NOT_USED(0),
  ID(1),
  NAME(2),
  CLASS_NAME(3),
  CSS_SELECTOR(4),
  TAG_NAME(5),
  XPATH(6),
  ACCESSIBILITY_ID(7),
  LINK_TEXT(8),
  PARTIAL_LINK_TEXT(9);

  private static final Map<Integer, FindByType> map = new HashMap<>();

  static {
    for (FindByType findByType : FindByType.values()) {
      map.put(findByType.id, findByType);
    }
  }

  @Getter
  private final int id;

  public static FindByType getType(Integer id) {
    return map.get(id);
  }

  public static FindByType getType(LocatorType locatorType) {
    switch (locatorType) {
      case xpath:
        return FindByType.XPATH;
      case csspath:
        return FindByType.CSS_SELECTOR;
      case id_value:
        return FindByType.ID;
      case name:
        return FindByType.NAME;
      case link_text:
        return FindByType.LINK_TEXT;
      case partial_link_text:
        return FindByType.PARTIAL_LINK_TEXT;
      case class_name:
        return FindByType.CLASS_NAME;
      case tag_name:
        return FindByType.TAG_NAME;
      case accessibility_id:
        return FindByType.ACCESSIBILITY_ID;
    }
    return null;
  }
}
