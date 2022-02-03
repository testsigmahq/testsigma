package com.testsigma.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TestPlanResultEvent<T> extends BaseEvent<T> {
  public String toString() {
    return super.toString();
  }
}
