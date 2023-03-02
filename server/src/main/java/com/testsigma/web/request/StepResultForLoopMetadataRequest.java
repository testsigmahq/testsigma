package com.testsigma.web.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.automator.entity.ForLoopConditionsEntity;
import com.testsigma.model.ForLoopCondition;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepResultForLoopMetadataRequest {

  private Integer index;
  private String iteration;
  private String testDataName;
  private ForLoopConditionRequest forLoopCondition;
}
