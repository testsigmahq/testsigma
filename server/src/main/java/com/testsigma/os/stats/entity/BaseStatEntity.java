package com.testsigma.os.stats.entity;

import com.testsigma.os.stats.event.EventType;
import lombok.Data;

@Data
public abstract class BaseStatEntity {
  private String serverUuid;
  private EventType eventType;
}
