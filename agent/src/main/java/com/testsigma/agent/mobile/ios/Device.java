package com.testsigma.agent.mobile.ios;

import lombok.Data;

@Data
public class Device {
  public String serialNumber;
  public String productId;
  public String locationId;
  public Integer deviceId;
  public String connectionType;
}
