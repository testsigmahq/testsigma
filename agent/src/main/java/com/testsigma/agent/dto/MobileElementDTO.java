/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */

package com.testsigma.agent.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class MobileElementDTO {
  private String id;
  private String uuid;
  private String name;
  private String value;
  private String type;
  private String xpath;
  private Boolean enabled;
  private Boolean visible;
  private Integer index;
  private Integer x1;
  private Integer y1;
  private Integer x2;
  private Integer y2;
  private String contentDesc;
  private String resourceId;
  private Boolean password;
  private Boolean clickable;
  private Boolean checked;
  private Boolean longClickable;
  private Boolean selected;
  private Boolean scrollable;
  private Boolean checkable;
  private Boolean focusable;
  private String text;
  private String packageName;
  private String label;
  private Boolean valid;
  private Integer width;
  private Integer height;
  private Integer depth;
  private String accessibilityId;
  private List<MobileElementDTO> childElements;
  private String webViewName;
  private Set<String> contextNames;
  private List<MobileWebElementDTO> webViewElements;
  private Map<String, Object> attributes;
}
