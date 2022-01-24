package com.testsigma.dto;

import com.testsigma.model.WorkspaceType;
import lombok.Data;

import java.util.List;

@Data
public class AddonNaturalTextActionEntityDTO {
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
  private List<AddonNaturalTextActionParameterEntityDTO> pluginParameters;
}
