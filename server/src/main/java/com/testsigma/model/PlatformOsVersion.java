package com.testsigma.model;

import lombok.Data;

@Data
public class PlatformOsVersion {
  private Long id;
  private Platform platform;
  private WorkspaceType workspaceType;
  private String name;
  private String version;
  private String displayName;
  private String platformVersion;
}
