package com.testsigma.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TestDataPropertiesEntityDTO {
  private String testDataName;
  private String testDataValue;
  private String testDataType;
  private Map<String, Object> testDataFunction;
  private String testDataValuePreSignedURL;
  private DefaultDataGeneratorsEntityDTO defaultDataGeneratorsEntity;
}
