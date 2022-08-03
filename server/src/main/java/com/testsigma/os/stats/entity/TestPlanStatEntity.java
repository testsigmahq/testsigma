package com.testsigma.os.stats.entity;

import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestPlanStatEntity extends BaseStatEntity {
  private Long testPlanId;
  private String entityType;
}
