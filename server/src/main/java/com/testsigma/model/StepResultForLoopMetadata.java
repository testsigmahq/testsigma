package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResultForLoopMetadata {

  private Integer index;
  private String iteration;
  @JsonProperty("testdata")
  private String testDataName;
}
