package com.testsigma.event;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class BaseEvent<T> {
  T eventData;
  EventType eventType;

  public String toString() {
    return String.format("[eventType: %s, model: %s", eventType, eventData.toString());
  }
}
