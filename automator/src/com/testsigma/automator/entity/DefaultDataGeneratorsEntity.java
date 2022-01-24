package com.testsigma.automator.entity;

import com.testsigma.model.CustomFunctionType;
import lombok.Data;

import java.util.Map;

@Data
public class DefaultDataGeneratorsEntity {
  private Long id;
  private String classPackage;
  private String className;
  private String functionName;
  private Integer lib;
  private Map<String, String> arguments;
  private Map<String, String> argumentTypes;
  private Boolean isKibbutzFn = false;
  private CustomFunctionType customFunctionType;
}
