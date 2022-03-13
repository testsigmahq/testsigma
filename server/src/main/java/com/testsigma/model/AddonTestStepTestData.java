package com.testsigma.model;

import lombok.Data;

import java.util.Map;

@Data
public class AddonTestStepTestData {
  private TestDataType type;
  private String value;
  private Map<String, String> testDataFunctionArguments;
  private Long testDataFunctionId;
  private Boolean isAddonFn;

}
