package com.testsigma.web.request;


import com.testsigma.automator.entity.DefaultDataGeneratorsEntity;
import lombok.Data;

import java.util.Map;

@Data
public class TestDataFunctionEntityRequest {
  private Long id;
  private String classPackage;
  private String className;
  private String functionName;
  private String binaryFileUrl;
  private DefaultDataGeneratorsEntity customFunctionType;
  private Integer lib;
  private Map<String, String> arguments;
  private Map<String, String> argumentTypes;
  private Boolean isAddonFn = false;
}
