package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResultForLoopMetadataRequest {

  private Integer index;
  private String iteration;
  private String testDataName;
}
