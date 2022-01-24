package com.testsigma.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class AttributePropertiesEntityDTO {
  private String attributeName;
  private String actionVariableName;
}
