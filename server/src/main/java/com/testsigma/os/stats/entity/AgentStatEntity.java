package com.testsigma.os.stats.entity;

import com.testsigma.model.AgentOs;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AgentStatEntity extends BaseStatEntity {
  private Long agentId;
  private AgentOs agentOs;
}
