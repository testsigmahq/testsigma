package com.testsigma.automator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestStepForLoopEntity {
  private int startIndex;
  private int endIndex;
  private Long testDataId;
}
