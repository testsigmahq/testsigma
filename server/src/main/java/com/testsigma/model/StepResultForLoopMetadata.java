package com.testsigma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.testsigma.dto.ForLoopConditionDTO;
import com.testsigma.web.request.ForLoopConditionRequest;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResultForLoopMetadata {

  private Integer index;
  private String iteration;
  @JsonProperty("testdata")
  private String testDataName;
  private ForLoopConditionDTO forLoopCondition;
}
