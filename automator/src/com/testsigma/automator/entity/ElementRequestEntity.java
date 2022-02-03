package com.testsigma.automator.entity;

import lombok.Data;

@Data
public class ElementRequestEntity {
  private String locatorValue;
  private String attributes;
  private String metadata;
  private Long workspaceVersionId;
}
