package com.testsigma.os.stats.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestCaseStatEntity extends BaseStatEntity {
  private Long testCaseId;
}
