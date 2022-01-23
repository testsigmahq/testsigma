package com.testsigma.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DefaultDataGeneratorsEntityDTO {
  private Long id;
  private String classPackage;
  private String className;
  private String functionName;
  private Integer lib;
  private Map<String, Object> arguments;
  private Map<String, String> argumentTypes;
}
