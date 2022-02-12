package com.testsigma.automator.entity;


import lombok.Data;

import java.util.List;

@Data
public class AddonPluginTestDataFunctionEntity {
  private Long id;
  private String displayName;
  private String description;
  private Boolean deprecated;
  private String classPath;
  private String fullyQualifiedName;
  private String version;
  private String modifiedHash;
  private String externalInstalledVersionUniqueId;
  private List<AddonPluginTestDataFunctionParameterEntity> pluginParameters;
}
