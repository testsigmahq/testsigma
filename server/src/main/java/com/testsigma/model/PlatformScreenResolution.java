package com.testsigma.model;

import lombok.Data;

@Data
public class PlatformScreenResolution {
  private Long id;
  private Platform platform;
  private String osName;
  private String osVersion;
  private String resolution;
  private String displayResolution;
}
