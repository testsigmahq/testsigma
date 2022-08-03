package com.testsigma.os.stats.entity;

import com.testsigma.model.WorkspaceType;
import com.testsigma.model.TestPlanLabType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestPlanRunStatEntity extends BaseStatEntity {
  private Long testPlanRunId;
  private String testPlanType;
}
