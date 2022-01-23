package com.testsigma.os.stats.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EnvironmentStatEntity extends BaseStatEntity {
  private Long environmentId;
}
