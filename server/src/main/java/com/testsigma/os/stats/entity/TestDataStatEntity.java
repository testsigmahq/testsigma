package com.testsigma.os.stats.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestDataStatEntity extends BaseStatEntity {
  private Long testDataId;
}
