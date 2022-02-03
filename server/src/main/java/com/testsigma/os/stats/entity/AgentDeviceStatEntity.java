package com.testsigma.os.stats.entity;

import com.testsigma.model.MobileOs;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AgentDeviceStatEntity extends BaseStatEntity {
  private Long agentDeviceId;
  private MobileOs agentDeviceOs;
}
