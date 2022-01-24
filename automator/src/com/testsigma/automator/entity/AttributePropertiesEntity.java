package com.testsigma.automator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class AttributePropertiesEntity {
  private String attributeName;
  private String actionVariableName;
}
