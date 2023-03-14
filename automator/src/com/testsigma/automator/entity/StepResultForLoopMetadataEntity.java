package com.testsigma.automator.entity;

import lombok.Data;

@Data
public class StepResultForLoopMetadataEntity {

  private int index;
  private ForLoopConditionsEntity forLoopConditions;
  private String iteration;
  private String testDataName;
  private Boolean emptyIterations;
}
