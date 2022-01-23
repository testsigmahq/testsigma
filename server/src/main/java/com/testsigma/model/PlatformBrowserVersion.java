package com.testsigma.model;

import lombok.Data;

@Data
public class PlatformBrowserVersion {
  private Long id;
  private Platform platform;
  private String osName;
  private String osVersion;
  private Browsers name;
  private String version;
  private String driverVersion;
  private String displayVersion;
}
