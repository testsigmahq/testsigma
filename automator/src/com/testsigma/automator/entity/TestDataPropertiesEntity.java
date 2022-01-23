package com.testsigma.automator.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
@EqualsAndHashCode
public class TestDataPropertiesEntity {
  private String testDataName;
  private String testDataValue;
  private String testDataType;
  private Map<String, Object> testDataFunction;
  private String testDataValuePreSignedURL;
  private Boolean hasPassword;
  private DefaultDataGeneratorsEntity defaultDataGeneratorsEntity;
}
