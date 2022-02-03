package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResultWhileLoopMetadataRequest {

  private Integer index;
}
