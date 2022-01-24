package com.testsigma.automator.entity;

import lombok.Data;

@Data
public class TestStepWhileLoopEntity {

  private Long testDataId;

  public Long getTestDataId() {
    if (testDataId == null)
      return 0L;
    return testDataId;

  }
}
