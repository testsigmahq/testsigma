package com.testsigma.dto;


import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class KibbutzPluginTestDataFunctionEntityDTO {
  private Long id;
  private String displayName;
  private String description;
  private Boolean deprecated;
  private String classPath;
  private String fullyQualifiedName;
  private String version;
  private String modifiedHash;
  private String externalInstalledVersionUniqueId;
  private List<KibbutzPluginTestDataFunctionParameterEntityDTO> pluginParameters;
}

