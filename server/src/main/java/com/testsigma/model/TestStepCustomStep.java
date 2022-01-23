package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepCustomStep {

  private Long id;
  @JsonProperty("class")
  private String className;
  @JsonProperty("function")
  private String functionName;
  @JsonProperty("binary_file_url")
  private String binaryFileUrl;
  @JsonProperty("type")
  private Integer type;
  @JsonProperty("args")
  private Map<String, String> arguments;
  @JsonProperty("args_types")
  private Map<String, String> argumentTypes;
}
