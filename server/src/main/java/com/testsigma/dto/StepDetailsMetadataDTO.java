package com.testsigma.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testsigma.model.TestStepPriority;
import com.testsigma.model.TestStepType;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StepDetailsMetadataDTO {

  private TestStepType type;
  private String action;
  private TestStepPriority priority;
}
