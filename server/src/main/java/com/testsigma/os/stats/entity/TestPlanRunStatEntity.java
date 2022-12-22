package com.testsigma.os.stats.entity;

import com.testsigma.model.TestPlanLabType;
import com.testsigma.model.WorkspaceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestPlanRunStatEntity extends BaseStatEntity {
  private Long testPlanRunId;
  private String testPlanType;
  private WorkspaceType applicationType;
  private TestPlanLabType testPlanLabType;
}
