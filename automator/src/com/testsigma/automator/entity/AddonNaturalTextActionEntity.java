package com.testsigma.automator.entity;

import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionEntity {
  private Long id;
  private String naturalText;
  private String description;
  private WorkspaceType workspaceType;
  private Boolean deprecated;
  private String classPath;
  private String fullyQualifiedName;
  private String version;
  private String modifiedHash;
  private String externalInstalledVersionUniqueId;
  private List<AddonNaturalTextActionParameterEntity> pluginParameters;
}
