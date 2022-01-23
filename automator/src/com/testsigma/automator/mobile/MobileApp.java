package com.testsigma.automator.mobile;

import lombok.Data;

@Data
public class MobileApp {
  private String name;
  private String version;
  private String bundleId;
  private String appPackage;
  private String appActivity;
  private MobileAppType appType;
}
