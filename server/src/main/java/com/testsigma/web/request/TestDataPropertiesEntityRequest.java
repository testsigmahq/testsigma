package com.testsigma.web.request;

import lombok.Data;

import java.util.Map;

@Data
public class TestDataPropertiesEntityRequest {
  private String testDataName;
  private String testDataValue;
  private String testDataType;
  private Map<String, Object> testDataFunction;
  private String testDataValuePreSignedURL;
  private Boolean hasPassword;
  private TestDataFunctionEntityRequest testDataFunctionEntity;
}
