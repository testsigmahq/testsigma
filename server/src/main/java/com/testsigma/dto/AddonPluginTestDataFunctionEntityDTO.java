package com.testsigma.dto;


import lombok.Data;

import java.util.List;

@Data
public class AddonPluginTestDataFunctionEntityDTO {
  private Long id;
  private String displayName;
  private String description;
  private Boolean deprecated;
  private String classPath;
  private String fullyQualifiedName;
  private String version;
  private String modifiedHash;
  private String externalInstalledVersionUniqueId;
  private List<AddonPluginTestDataFunctionParameterEntityDTO> pluginParameters;
}

