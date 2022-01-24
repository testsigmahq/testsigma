package com.testsigma.os.stats.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestStepStatEntity extends BaseStatEntity {
  private Long testStepId;
  private Long testCaseId;
}
