package com.testsigma.dto.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CloudTestDataFunction {
  @JsonProperty("id")
  private Long id;
  @JsonProperty("package")
  private String classPackage;
  @JsonProperty("class")
  private String className;
  @JsonProperty("function")
  private String functionName;
  @JsonProperty("args")
  private Map<String, String> testDataFunctionArgs;
  @JsonProperty("args_types")
  private Map<String, String> argumentTypes;
  @JsonProperty("lib")
  private Integer lib;
  @JsonProperty("type")
  private Integer type;
  @JsonProperty("binary_file_url")
  private String binaryFileUrl;
}
