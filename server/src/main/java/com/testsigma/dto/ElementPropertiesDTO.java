package com.testsigma.dto;

import com.testsigma.model.FindByType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class ElementPropertiesDTO {
  private String elementName;
  private String locatorValue;
  private String locatorStrategyName;
  private FindByType findByType;
  private ElementDTO elementEntity;
  private String actionVariablename;

}
