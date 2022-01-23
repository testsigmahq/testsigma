package com.testsigma.agent.mobile.ios;

import lombok.Data;

@Data
public class DeviceConnectionMessage {
  public Device device;
  public Type type;

  public enum Type {
    Add, Remove
  }
}
